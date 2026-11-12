package com.example.MPAZpannol.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

class RegisterViewModel : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChanged(value: String) {
        uiState = uiState.copy(
            name = value,
            nameError = null
        )
    }

    fun onEmailChanged(value: String) {
        uiState = uiState.copy(
            email = value,
            emailError = null
        )
    }

    fun onPasswordChanged(value: String) {
        uiState = uiState.copy(
            password = value,
            passwordError = null,
            confirmPasswordError = null
        )
    }

    fun onConfirmPasswordChanged(value: String) {
        uiState = uiState.copy(
            confirmPassword = value,
            confirmPasswordError = null
        )
    }

    fun validateRegister(): Boolean {
        val name = uiState.name.trim()
        val email = uiState.email.trim()
        val password = uiState.password
        val confirmation = uiState.confirmPassword

        val nameError = when {
            name.isEmpty() -> "El nombre es obligatorio"
            name.length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

        val emailError = when {
            email.isEmpty() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Ingresa un correo válido"
            else -> null
        }

        val passwordError = when {
            password.isEmpty() -> "La contraseña es obligatoria"
            password.length < 6 ->
                "Mínimo 6 caracteres"
            else -> null
        }

        val confirmError = when {
            confirmation.isEmpty() ->
                "Confirma tu contraseña"
            confirmation != password ->
                "Las contraseñas no coinciden"
            else -> null
        }

        uiState = uiState.copy(
            name = name,
            email = email,
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmError
        )

        return nameError == null &&
                emailError == null &&
                passwordError == null &&
                confirmError == null
    }
}
