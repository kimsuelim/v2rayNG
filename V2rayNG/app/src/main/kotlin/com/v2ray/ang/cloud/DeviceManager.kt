package com.v2ray.ang.cloud

import android.util.Log
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.cloud.Http.getApiHost
import com.v2ray.ang.cloud.dto.DeviceDto
import com.v2ray.ang.util.MmkvManager
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object DeviceManager {
    private const val DEVICE_UUID = "device_uuid"

    private val deviceStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_DEVICE, MMKV.MULTI_PROCESS_MODE) }

    suspend fun activateDevice(): Any {
        return withContext(Dispatchers.IO) {
            try {
                val url = getApiHost() + "/devices"
                val deviceDto = DeviceDto(
                    uuid = getDeviceUuid(),
                    networkInfo = SysInfo.networkInfo(),
                    softwareInfo = SysInfo.softwareInfo()
                )

                val jsonString = Gson().toJson(deviceDto)
                val resp = Http.post(url, jsonString)
                Log.i("Device", "activateDevice: $resp")

                setDeviceUuid(deviceDto.uuid)
            } catch (e: Exception) {
                Sentry.captureException(e)
                Log.d("Device", "activateDevice", e)
            }
        }
    }

    suspend fun getActivatedDevice(): Any {
        return withContext(Dispatchers.IO) {
            try {
                val url = getApiHost() + "/devices" + "/" + getDeviceUuid()
                val resp = Http.get(url)
                Log.i("Device", "getActivatedDevice: $resp")
            } catch (e: Exception) {
                Sentry.captureException(e)
                Log.d("Device", "getActivatedDevice", e)
            }
        }
    }

    private fun setDeviceUuid(uuid: String) {
        deviceStorage.encode(DEVICE_UUID, uuid)
    }

    fun getDeviceUuid(): String {
        return deviceStorage.decodeString(DEVICE_UUID, UUID.randomUUID().toString())!!
    }

    fun clearDeviceUuid() {
        deviceStorage.encode(DEVICE_UUID, "")
    }

    fun isActivated(): Boolean {
        return !deviceStorage.decodeString(DEVICE_UUID).isNullOrBlank()
    }
}