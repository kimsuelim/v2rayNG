package com.v2ray.ang.cloud

import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.v2ray.ang.BuildConfig
import libv2ray.Libv2ray
import java.lang.System.currentTimeMillis
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


object SysInfo {
    fun hostInfo(): Map<String, Any> {
        val bootTime = currentTimeMillis() - SystemClock.elapsedRealtime()
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val formatedBootTime = dateFormat.format(bootTime)

        val info = mapOf(
            "hostname" to "",
            "bootTime" to formatedBootTime,
            "os" to "android",
            "platform" to "android",
            "platformFamily" to "android",
            "platformVersion" to Build.VERSION.RELEASE,
            "kernelVersion" to System.getProperty("os.version"),
            "kernelArch" to Build.SUPPORTED_ABIS.first(),
            "timezone"  to TimeZone.getDefault().id,
            "timezoneOffsetSec" to TimeZone.getDefault().rawOffset / 1000,
            "deviceName" to Build.MODEL,
            "deviceModel" to "${Build.MODEL} (${Build.ID})",
            "deviceBrand" to Build.BRAND
        )

        return info
    }

    fun softwareInfo(): Map<String, Any> {
        return mapOf(
            "os" to "Android ${Build.VERSION.RELEASE}",
            "arch" to Build.SUPPORTED_ABIS.first(),
            "goVersion" to "",
            "swVersion" to BuildConfig.VERSION_NAME,
            "v2rayVersion" to getV2rayVersion(),
        )
    }

    fun networkInfo(): List<Map<String, Any>> {
        return getLocalIpAddress()
    }

    private fun getLocalIpAddress(): List<Map<String, Any>> {
        val netInfos: MutableList<HashMap<String, Any>> = mutableListOf()

        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val ipAddr = intf.getInetAddresses()
                while (ipAddr.hasMoreElements()) {
                    val inetAddress = ipAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        netInfos.add(hashMapOf("name" to intf.name, "ipAddr" to inetAddress.hostAddress!!, "hardwareAddr" to ""))
                    }
                }
            }
        } catch (e: SocketException) {
            Log.d("SysInfo", "getLocalIpAddress", e)
        }

        return netInfos
    }

    private fun getV2rayVersion(): String {
        return Libv2ray.checkVersionX()
    }
}