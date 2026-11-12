package com.example.MPAZpannol.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.ResultadoRegistro
import com.example.MPAZpannol.data.UsuarioRepository
import com.example.MPAZpannol.data.local.MPAZDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val registerError: String? = null,
    val isSaving: Boolean = false,
    val registered: Boolean = false
)

class RegisterViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(
        MPAZDatabase.getInstance(application).usuarioDao()
    )

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChanged(value: String) {
        uiState = uiState.copy(
            name = value,
            nameError = null,
            registerError = null
        )
    }

    fun onEmailChanged(value: String) {
        uiState = uiState.copy(
            email = value,
            emailError = null,
            registerError = null
        )
    }

    fun onPasswordChanged(value: String) {
        uiState = uiState.copy(
            password = value,
            passwordError = null,
            confirmPasswordError = null,
            registerError = null
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
            password.isEmpty() ->
                "La contraseña es obligatoria"
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

    fun registrarUsuario() {

        if (uiState.isSaving || !validateRegister()) {
            return
        }

        val nombre = uiState.name.trim()
        val correo = uiState.email.trim()
            .lowercase(Locale.ROOT)
        val password = uiState.password

        uiState = uiState.copy(
            isSaving = true,
            registerError = null
        )

        viewModelScope.launch {

            try {
                val resultado = withContext(Dispatchers.IO) {
                    repository.registrarUsuario(
                        nombre,
                        correo,
                        password
                    )
                }

                when (resultado) {

                    ResultadoRegistro.EXITO -> {
                        uiState = uiState.copy(
                            isSaving = false,
                            registered = true,
                            password = "",
                            confirmPassword = ""
                        )
                    }

                    ResultadoRegistro.CORREO_DUPLICADO -> {
                        uiState = uiState.copy(
                            isSaving = false,
                            emailError =
                                "Este correo ya está registrado"
                        )
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    registerError =
                        "No se pudo guardar la cuenta"
                )
            }
        }
    }
}
