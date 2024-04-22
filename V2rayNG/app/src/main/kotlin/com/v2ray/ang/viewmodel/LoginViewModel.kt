package com.v2ray.ang.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.cloud.User
import com.v2ray.ang.cloud.UserManager

class LoginViewModel : ViewModel() {
    var appJustLaunched by mutableStateOf(true)
    var userIsAuthenticated by mutableStateOf(false)
    val isAuthenticated by lazy { MutableLiveData(false) }
    var user by mutableStateOf(User())

    private val TAG = "LoginViewModel"
    private lateinit var account: Auth0
    private lateinit var context: Context

    fun setContext(activityContext: Context) {
        context = activityContext
        account = Auth0(
            BuildConfig.AUTH0_CLIENT_ID,
            BuildConfig.AUTH0_DOMAIN
        )
    }

    fun login() {
        WebAuthProvider
            .login(account)
            .withScheme("app")
            .start(context, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // The user either pressed the “Cancel” button
                    // on the Universal Login screen or something
                    // unusual happened.
                    Log.e(TAG, "Error occurred in login(): $error")
                }

                override fun onSuccess(result: Credentials) {
                    // The user successfully logged in.
                    val idToken = result.idToken

                    // TODO: 🚨 REMOVE BEFORE GOING TO PRODUCTION!
                    Log.d(TAG, "ID token: $idToken")

                    user = User(idToken)
                    UserManager.setDeviceUser(user) // Keep order
                    userIsAuthenticated = true
                    isAuthenticated.value = true
                    appJustLaunched = false
                }
            })
    }

    fun logout() {
        WebAuthProvider
            .logout(account)
            .withScheme("app")
            .start(context, object : Callback<Void?, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // For some reason, logout failed.
                    Log.e(TAG, "Error occurred in logout(): $error")
                }

                override fun onSuccess(result: Void?) {
                    // The user successfully logged out.
                    user = User()
                    UserManager.clearDeviceUser()
                    userIsAuthenticated = false
                    isAuthenticated.value = false
                }
            })
    }
}