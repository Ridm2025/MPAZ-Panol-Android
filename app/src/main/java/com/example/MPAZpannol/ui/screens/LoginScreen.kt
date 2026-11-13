package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MPAZpannol.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (Long, String, String) -> Unit,
    onBackClick: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val state = loginViewModel.uiState

    var showPassword by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) {
            onLoginSuccess(
                state.usuarioId,
                state.nombreUsuario,
                state.rolUsuario
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Bienvenido a MPAZ Pañol")

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = loginViewModel::onEmailChanged,
            label = { Text("Correo electrónico") },
            singleLine = true,
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let {
                    Text("⚠ $it")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = loginViewModel::onPasswordChanged,
            label = { Text("Contraseña") },
            singleLine = true,
            isError = state.passwordError != null,
            supportingText = {
                state.passwordError?.let {
                    Text("⚠ $it")
                }
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        showPassword = !showPassword
                    }
                ) {
                    Text(
                        if (showPassword) "Ocultar"
                        else "Mostrar"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        state.loginError?.let { error ->

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.iniciarSesion()
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
