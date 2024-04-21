package com.v2ray.ang.cloud

import android.os.Build
import android.util.Log
import com.v2ray.ang.AppConfig
import com.v2ray.ang.BuildConfig
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


object SysInfo {
    fun softwareInfo(): Map<String, Any> {
        return mapOf(
            "os" to "Android ${Build.VERSION.RELEASE}",
            "arch" to Build.SUPPORTED_ABIS.first(),
            "goVersion" to "go1.22.2", // TODO: get version?
            "swVersion" to BuildConfig.VERSION_NAME
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
            Log.d(AppConfig.ANG_PACKAGE, "getLocalIpAddress", e)
        }

        return netInfos
    }
}