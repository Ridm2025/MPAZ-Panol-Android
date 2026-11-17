package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.ReservaRepository
import com.example.MPAZpannol.data.local.MPAZDatabase
import com.example.MPAZpannol.data.local.ReservaPendienteAdmin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class SolicitudesPendientesUiState(
    val solicitudes: List<ReservaPendienteAdmin> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class SolicitudesPendientesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        MPAZDatabase.getInstance(application)
    )

    var uiState by mutableStateOf(
        SolicitudesPendientesUiState()
    )
        private set

    init {
        cargarSolicitudes()
    }

    private fun cargarSolicitudes() {

        viewModelScope.launch {

            repository.observarReservasPendientes()
                .catch {
                    uiState = SolicitudesPendientesUiState(
                        isLoading = false,
                        error = "No se pudieron cargar las solicitudes"
                    )
                }
                .collect { reservas ->

                    uiState = SolicitudesPendientesUiState(
                        solicitudes = reservas,
                        isLoading = false
                    )
                }
        }
    }
}
