package com.example.MPAZpannol.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChanged(value: String) {
        uiState = uiState.copy(
            email = value,
            emailError = null
        )
    }

    fun onPasswordChanged(value: String) {
        uiState = uiState.copy(
            password = value,
            passwordError = null
        )
    }

    fun validateLogin(): Boolean {
        val email = uiState.email.trim()
        val password = uiState.password

        val emailError = when {
            email.isEmpty() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Ingresa un correo válido"
            else -> null
        }

        val passwordError = when {
            password.isEmpty() -> "La contraseña es obligatoria"
            password.length < 6 ->
                "Debe tener al menos 6 caracteres"
            else -> null
        }

        uiState = uiState.copy(
            email = email,
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }
}
