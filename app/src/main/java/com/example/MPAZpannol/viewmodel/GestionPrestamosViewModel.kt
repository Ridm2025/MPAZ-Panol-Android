package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.ReservaRepository
import com.example.MPAZpannol.data.local.MPAZDatabase
import com.example.MPAZpannol.data.local.ReservaGestionAdmin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GestionPrestamosUiState(
    val prestamos: List<ReservaGestionAdmin> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val mensaje: String? = null
)

class GestionPrestamosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        MPAZDatabase.getInstance(application)
    )

    var uiState by mutableStateOf(GestionPrestamosUiState())
        private set

    var procesandoId by mutableStateOf<Long?>(null)
        private set

    init {
        viewModelScope.launch {
            repository.observarPrestamosEnGestion()
                .catch {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los préstamos"
                    )
                }
                .collect { prestamos ->
                    uiState = uiState.copy(
                        prestamos = prestamos,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun avanzarEstado(
        administradorId: Long,
        reservaId: Long
    ) {
        if (procesandoId != null) return

        procesandoId = reservaId

        uiState = uiState.copy(
            mensaje = null,
            error = null
        )

        viewModelScope.launch {
            try {
                val nuevoEstado = withContext(Dispatchers.IO) {
                    repository.avanzarEstadoPrestamo(
                        administradorId,
                        reservaId
                    )
                }

                uiState = uiState.copy(
                    mensaje = "Solicitud #$reservaId: $nuevoEstado"
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: IllegalArgumentException) {
                uiState = uiState.copy(error = e.message)

            } catch (e: IllegalStateException) {
                uiState = uiState.copy(error = e.message)

            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = "No se pudo actualizar el préstamo"
                )

            } finally {
                procesandoId = null
            }
        }
    }
}
