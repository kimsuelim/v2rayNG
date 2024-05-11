package com.v2ray.ang.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.v2ray.ang.R
import com.v2ray.ang.cloud.Http
import com.v2ray.ang.cloud.UserManager
import com.v2ray.ang.cloud.UserManager.getDeviceAdmin
import com.v2ray.ang.cloud.UserNotAuthorizedException
import com.v2ray.ang.cloud.dto.LoginResponseDto
import com.v2ray.ang.cloud.dto.UserDto
import com.v2ray.ang.cloud.dto.LoginRequestDto
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
                        val loginParams = LoginRequestDto(
                            email = userName,
                            password = userPassword
                        )
                        val jsonString = Gson().toJson(loginParams)
                        val resp = Http.post(url, jsonString)

                        val respDto = Gson().fromJson(resp, LoginResponseDto::class.java)
                        val jwt = JWT(respDto.token)
                        val user = UserDto(
                            id = jwt.getClaim("user_id").asLong()!!,
                            name = jwt.getClaim("name").asString()!!,
                            email = jwt.getClaim("email").asString()!!,
                            password = respDto.token
                        )

                        signed(user)
                    }
                } catch (e: UserNotAuthorizedException) {
                    Log.e(TAG, "Invalid email or password: $e")
                    showToast.postValue(R.string.error_sign_in.toString())
                } catch (e: IOException) {
                    Log.e(TAG, "Error occurred in login(): $e")
                    showToast.postValue(R.string.error_connecting_server.toString())
                } catch (e: Exception) {
                    Log.e(TAG, "Error occurred in login(): $e")
                    showToast.postValue(R.string.error_something_went_wrong.toString())
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
        isAuthenticated.postValue(true)
    }
}