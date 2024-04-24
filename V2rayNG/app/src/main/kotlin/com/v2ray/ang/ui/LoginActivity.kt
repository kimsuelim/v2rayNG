package com.v2ray.ang.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.v2ray.ang.viewmodel.LoginViewModel
import com.v2ray.ang.AppConfig
import com.v2ray.ang.cloud.UserManager
import com.v2ray.ang.cloud.UserManager.setDeviceAdmin
import com.v2ray.ang.cloud.ui.LoginScreen
import com.v2ray.ang.extension.toast
import io.sentry.Sentry


class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initDevice()
        installSplashScreen()

        val intent = Intent(this, MainActivity::class.java)
        if (UserManager.isAuthenticated()) {
            startActivity(intent)
            return
        }

        loginViewModel.showToast.observe(this) {
            if (it.isNotBlank()) toast(getString(it.toInt())) // avoid access string resource in viewmodel
        }

        loginViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            Log.d(AppConfig.ANG_PACKAGE, "LoginActivity onCreate: isAuthenticated= $isAuthenticated")

            if (isAuthenticated == false) return@observe

            val user = UserManager.getDeviceUser()
            Log.d(AppConfig.ANG_PACKAGE, "LoginActivity onCreate: user= $user")
            startActivity(intent)
        }

        setContent {
            //JetpackComposeLoginTheme {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(loginViewModel)
                }
            }
        }
    }

    private fun initDevice() {
        try {
            //UserManager.clearDeviceUser()
            setDeviceAdmin()
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }
}