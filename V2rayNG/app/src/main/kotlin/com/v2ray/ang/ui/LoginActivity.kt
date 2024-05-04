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
import androidx.lifecycle.lifecycleScope
import com.v2ray.ang.viewmodel.LoginViewModel
import com.v2ray.ang.cloud.ServerManager
import com.v2ray.ang.cloud.UserManager
import com.v2ray.ang.cloud.UserManager.setDeviceAdmin
import com.v2ray.ang.cloud.ui.LoginScreen
import com.v2ray.ang.extension.toast
import io.sentry.Sentry
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        initDevice()
        syncServers()

        val intent = Intent(this, MainActivity::class.java)
        if (UserManager.isAuthenticated()) {
            startActivity(intent)
            return
        }

        loginViewModel.showToast.observe(this) {
            if (it.isNotBlank()) toast(getString(it.toInt())) // avoid access string resource in viewmodel
        }

        loginViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            Log.d("LoginActivity", "onCreate: isAuthenticated=$isAuthenticated")

            if (isAuthenticated == false) return@observe

            val user = UserManager.getDeviceUser()
            Log.d("LoginActivity", "LoginActivity onCreate: user=$user")
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
            setDeviceAdmin()
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun syncServers() {
        lifecycleScope.launch {
            ServerManager.syncServerWithCloud()
        }
    }
}