package com.v2ray.ang.cloud

import android.util.Log
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.cloud.Http.getApiHost
import com.v2ray.ang.cloud.dto.DeviceDto
import com.v2ray.ang.cloud.dto.ManageDeviceDto
import com.v2ray.ang.util.MmkvManager
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object DeviceManager {
    private const val DEVICE_UUID = "device_uuid"

    private val deviceStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_DEVICE, MMKV.MULTI_PROCESS_MODE) }

    suspend fun registerDevice(): Any {
        return withContext(Dispatchers.IO) {
            try {
                val url = getApiHost() + "/devices"
                val deviceDto = DeviceDto(
                    uuid = getDeviceUUID(),
                    hostInfo = SysInfo.hostInfo(),
                    networkInfo = SysInfo.networkInfo(),
                    softwareInfo = SysInfo.softwareInfo()
                )

                val jsonString = Gson().toJson(deviceDto)
                val resp = Http.post(url, jsonString)
                Log.i("Device", "registerDevice: $resp")
            } catch (e: UserNotAuthorizedException) {
                Log.e("Device", "registerDevice", e)
                throw e
            } catch (e: Exception) {
                Sentry.captureException(e)
                Log.e("Device", "registerDevice", e)
            }
        }
    }

    suspend fun managingDevice(): Any {
        return withContext(Dispatchers.IO) {
            try {
                val url = getApiHost() + "/me/manage_devices"
                val manageDeviceDto = ManageDeviceDto(
                    uuid = getDeviceUUID(),
                )

                val jsonString = Gson().toJson(manageDeviceDto)
                val resp = Http.post(url, jsonString)
                Log.i("Device", "managingDevice: $resp")
            } catch (e: UserNotAuthorizedException) {
                Log.e("Device", "managingDevice", e)
                throw e
            } catch (e: Exception) {
                Sentry.captureException(e)
                Log.e("Device", "managingDevice", e)
            }
        }
    }

    suspend fun getActivatedDevice(): Any {
        return withContext(Dispatchers.IO) {
            try {
                val url = getApiHost() + "/devices" + "/" + getDeviceUUID()
                val resp = Http.get(url)
                Log.i("Device", "getActivatedDevice: $resp")
            } catch (e: Exception) {
                Sentry.captureException(e)
                Log.d("Device", "getActivatedDevice", e)
            }
        }
    }

     fun setDeviceUUID() {
        if (isInitialized()) return
        setDeviceUUID(UUID.randomUUID().toString())
    }

    fun setDeviceUUID(uuid: String) {
        deviceStorage.encode(DEVICE_UUID, uuid)
    }

    fun getDeviceUUID(): String {
        return deviceStorage.decodeString(DEVICE_UUID)!!
    }

    fun clearDeviceUUID() {
        deviceStorage.encode(DEVICE_UUID, "")
    }

    private fun isInitialized(): Boolean {
        return !deviceStorage.decodeString(DEVICE_UUID).isNullOrBlank()
    }
}