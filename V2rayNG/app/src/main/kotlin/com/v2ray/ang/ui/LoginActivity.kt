package com.v2ray.ang.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil.compose.rememberAsyncImagePainter
import com.v2ray.ang.viewmodel.LoginViewModel
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.cloud.UserManager


class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        val intent = Intent(this, MainActivity::class.java)
        if (UserManager.isAuthenticated()) {
            startActivity(intent)
            return
        }

        loginViewModel.setContext(this)

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
                    MainView(loginViewModel)
                }
            }
        }
    }
}

@Composable
fun MainView(viewModel: LoginViewModel) {
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val title = if (viewModel.userIsAuthenticated) {
            stringResource(R.string.logged_in_title)
        } else {
            if (viewModel.appJustLaunched) {
                stringResource(R.string.initial_title)
            } else {
                stringResource(R.string.logged_out_title)
            }
        }
        Title(text = title)

        if (viewModel.userIsAuthenticated) {
            UserInfoRow(
                label = stringResource(R.string.name_label),
                value = "Name goes here",
            )

            UserInfoRow(
                label = stringResource(R.string.email_label),
                value = "Email goes here",
            )

            UserPicture(
                url = "https://images.ctfassets.net/23aumh6u8s0i/5hHkO5DxWMPxDjc2QZLXYf/403128092dedc8eb3395314b1d3545ad/icon-user.png",
                description = "Description goes here",
            )
        }

        val buttonText: String
        val onClickAction: () -> Unit
        if (viewModel.userIsAuthenticated) {
            buttonText = stringResource(R.string.log_out_button)
            onClickAction = { viewModel.logout() }
        } else {
            buttonText = stringResource(R.string.log_in_button)
            onClickAction = { viewModel.login() }
        }
        LogButton(
            text = buttonText,
            onClick = onClickAction,
        )
    }
}

@Composable
fun Title(
    text: String,
) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
        )
    )
}

@Composable
fun UserInfoRow(
    label: String,
    value: String,
) {
    Row {  // 1
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        )
        Spacer(
            // 2
            modifier = Modifier.width(10.dp),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 20.sp,
            )
        )
    }
}

@Composable
fun UserPicture(
    // 1
    url: String,
    description: String,
) {
    Column(
        // 2
        modifier = Modifier
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            // 3
            painter = rememberAsyncImagePainter(url),
            contentDescription = description,
            modifier = Modifier
                .fillMaxSize(0.5f),
        )
    }
}

@Composable
fun LogButton(
    // 1
    text: String,
    onClick: () -> Unit,
) {
    Column(
        // 2
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            // 3
            onClick = { onClick() },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
        ) {
            Text(
                // 4
                text = text,
                fontSize = 20.sp,
            )
        }
    }
}