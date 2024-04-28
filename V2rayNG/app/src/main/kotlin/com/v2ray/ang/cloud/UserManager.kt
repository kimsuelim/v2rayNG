package com.v2ray.ang.cloud

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.cloud.dto.UserDto
import com.v2ray.ang.util.MmkvManager

object UserManager {
    private const val DEVICE_USER = "device_user"
    private const val DEVICE_ADMIN = "device_admin"

    private val deviceStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_DEVICE,
            MMKV.MULTI_PROCESS_MODE
        )
    }

    fun setDeviceUser(user: UserDto) {
        val jsonStr = Gson().toJson(user)
        deviceStorage.encode(DEVICE_USER, jsonStr)
    }

    fun clearDeviceUser() {
        deviceStorage.encode(DEVICE_USER, "")
    }

    fun getDeviceUser(): UserDto {
        val jsonString = deviceStorage.decodeString(DEVICE_USER)
        return Gson().fromJson(jsonString, UserDto::class.java)
    }

    fun isAuthenticated(): Boolean {
        return !deviceStorage.decodeString(DEVICE_USER).isNullOrBlank()
    }

    fun setDeviceAdmin() {
        val admin = UserDto(
            name = "admin",
            email = BuildConfig.ADMIN_USERNAME,
            password = BuildConfig.ADMIN_PASSWORD
        )

        val jsonStr = Gson().toJson(admin)
        deviceStorage.encode(DEVICE_ADMIN, jsonStr)
    }

    fun getDeviceAdmin() : UserDto {
        val jsonString = deviceStorage.decodeString(DEVICE_ADMIN)
        return Gson().fromJson(jsonString, UserDto::class.java)
    }

    fun clearDeviceAdmin() {
        deviceStorage.encode(DEVICE_ADMIN, "")
    }
}