package com.v2ray.ang.cloud

import android.util.Log

object TokenManager {
    private const val TAG = "TokenManager"

    fun getAccessToken(): String {
        return try {
            val userDto = UserManager.getDeviceUser()
            userDto.password
        } catch (e: NullPointerException) {
            Log.e(TAG, "Error occurred in getAccessToken(): $e") // when fresh login request
            ""
        }
    }
}