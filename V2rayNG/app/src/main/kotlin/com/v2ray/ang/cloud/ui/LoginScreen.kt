package com.v2ray.ang.cloud.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.viewmodel.LoginViewModel


@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    LoginScreen(
        userName = viewModel.userName,
        onUserNameChange = { viewModel.userName = it },
        userPassword = viewModel.userPassword,
        onUserPasswordChange = { viewModel.userPassword = it },
        onAuthenticate = { viewModel.login() },
        error = viewModel.error,
        onDismissError = { viewModel.error = "" }
    )
}

@Composable
fun LoginScreen(
    userName: String,
    onUserNameChange: (String) -> Unit = { },
    userPassword: String,
    onUserPasswordChange: (String) -> Unit = { },
    onAuthenticate: () -> Unit = { },
    error: String = "",
    onDismissError: () -> Unit = { },
    ) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(32.dp)
    ) {
        Text(
            text = stringResource(R.string.title_sign_in),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 32.dp)
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            label = { Text(text = stringResource(R.string.label_email)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            value = userName,
            onValueChange = onUserNameChange,
            //isError = userName.isNotEmpty() && !isValidText(userName)
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            label = { Text(text = stringResource(R.string.label_password)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAuthenticate() }
            ),
            visualTransformation = PasswordVisualTransformation(),
            value = userPassword,
            onValueChange = onUserPasswordChange,
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 32.dp),
            onClick = onAuthenticate,
        ) {
            Text(
                text = stringResource(R.string.action_sign_in),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }

    if (error.isNotBlank()) {
        AuthenticationErrorDialog(
            error = error,
            dismissError = onDismissError
        )
    }
}

fun isValidText(text: String): Boolean {
    return text.matches(Regex("[a-zA-Z]+"))
}

@Composable
fun AuthenticationErrorDialog(
    modifier: Modifier = Modifier,
    error: String,
    dismissError: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            dismissError()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dismissError()
                }
            ) {
                Text(text = stringResource(id = R.string.error_action)) }
        },
        title = {
            Text(
                text = stringResource(id = R.string.error_title),
                fontSize = 18.sp
            )
        },
        text = {
            Text(text = error)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // JetpackComposeLoginTheme
    MaterialTheme {
        LoginScreen(
            userName = "",
            userPassword = "",
        )
    }
}
