package com.example.MPAZpannol.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.UsuarioRepository
import com.example.MPAZpannol.data.local.MPAZDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoading: Boolean = false,
    val loggedIn: Boolean = false,
    val nombreUsuario: String = "",
    val rolUsuario: String = "",
    val usuarioId: Long = 0L,
)

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(
        MPAZDatabase.getInstance(application).usuarioDao()
    )

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEmailChanged(value: String) {
        uiState = uiState.copy(
            email = value,
            emailError = null,
            loginError = null
        )
    }

    fun onPasswordChanged(value: String) {
        uiState = uiState.copy(
            password = value,
            passwordError = null,
            loginError = null
        )
    }

    private fun validarLogin(): Boolean {

        val email = uiState.email.trim()
        val password = uiState.password

        val emailError = when {
            email.isEmpty() ->
                "El correo es obligatorio"

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        val passwordError = when {
            password.isEmpty() ->
                "La contraseña es obligatoria"

            password.length < 6 ->
                "Mínimo 6 caracteres"

            else -> null
        }

        uiState = uiState.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return emailError == null && passwordError == null
    }

    fun iniciarSesion() {

        if (uiState.isLoading || !validarLogin()) {
            return
        }

        val correo = uiState.email.trim()
            .lowercase(Locale.ROOT)

        val password = uiState.password

        uiState = uiState.copy(
            isLoading = true,
            loginError = null
        )

        viewModelScope.launch {

            try {
                val usuario = withContext(Dispatchers.IO) {
                    repository.iniciarSesion(
                        correo,
                        password
                    )
                }

                if (usuario != null) {

                    uiState = uiState.copy(
                        isLoading = false,
                        loggedIn = true,
                        nombreUsuario = usuario.nombre,
                        rolUsuario = usuario.rol,
                        usuarioId = usuario.id,
                        password = ""
                    )

                } else {

                    uiState = uiState.copy(
                        isLoading = false,
                        loginError =
                            "Correo o contraseña incorrectos"
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                uiState = uiState.copy(
                    isLoading = false,
                    loginError =
                        "No se pudo iniciar sesión"
                )
            }
        }
    }
}
