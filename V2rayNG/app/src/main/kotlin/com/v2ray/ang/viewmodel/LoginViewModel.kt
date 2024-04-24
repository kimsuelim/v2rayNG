package com.v2ray.ang.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.v2ray.ang.R
import com.v2ray.ang.cloud.Http
import com.v2ray.ang.cloud.UserManager
import com.v2ray.ang.cloud.UserManager.getDeviceAdmin
import com.v2ray.ang.cloud.dto.UserDto
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginViewModel : ViewModel() {
    var userName by mutableStateOf("")
    var userPassword by mutableStateOf("")
    var userIsAuthenticated by mutableStateOf(false)
    val isAuthenticated by lazy { MutableLiveData(false) }
    var error by mutableStateOf("")
    val showToast by lazy { MutableLiveData("") }

    private val TAG = "LoginViewModel"

    fun login() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (loginWithAdmin()) {
                        return@withContext
                    } else {
                        val url = Http.getApiHost() + "/login"
                        val userParams = UserDto(
                            name = userName,
                            email = userName,
                            password = userPassword
                        )
                        val jsonString = Gson().toJson(userParams)
                        val resp = Http.post(url, jsonString)
                        Log.i(TAG, "Resp: $resp")
                        val user = Gson().fromJson(resp, UserDto::class.java)

                        signed(user)
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Error occurred in login(): $e")
                    //error = R.string.error_sign_in.toString()
                    showToast.postValue(R.string.error_sign_in.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error occurred in login(): $e")
                    //error = "Something went wrong!"
                    showToast.postValue(R.string.error_sign_in.toString())
                    Sentry.captureException(e)
                }
            }
        }
    }

    private fun loginWithAdmin(): Boolean {
        val admin = getDeviceAdmin()
        if (!(admin.email == userName && admin.password == userPassword)) return false

        signed(admin)
        return true
    }

    private fun signed(user: UserDto) {
        UserManager.setDeviceUser(user) // Keep order
        userIsAuthenticated = true
        isAuthenticated.postValue(true) // Cannot invoke setValue on a background thread
    }

    fun logout() {
        UserManager.clearDeviceUser()
        userIsAuthenticated = false
        isAuthenticated.value = false
    }
}