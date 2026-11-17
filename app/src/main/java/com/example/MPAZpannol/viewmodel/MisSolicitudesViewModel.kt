package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.ReservaRepository
import com.example.MPAZpannol.data.local.MPAZDatabase
import com.example.MPAZpannol.data.local.ReservaResumen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class MisSolicitudesUiState(
    val solicitudes: List<ReservaResumen> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class MisSolicitudesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        MPAZDatabase.getInstance(application)
    )

    private var cargaJob: Job? = null

    var uiState by mutableStateOf(MisSolicitudesUiState())
        private set

    fun cargarSolicitudes(usuarioId: Long) {

        cargaJob?.cancel()

        if (usuarioId <= 0) {
            uiState = MisSolicitudesUiState(
                isLoading = false,
                error = "Debes iniciar sesión"
            )
            return
        }

        uiState = MisSolicitudesUiState()

        cargaJob = viewModelScope.launch {

            repository.observarReservasDeUsuario(usuarioId)
                .catch {
                    uiState = MisSolicitudesUiState(
                        isLoading = false,
                        error = "No se pudieron cargar las solicitudes"
                    )
                }
                .collect { reservas ->

                    uiState = MisSolicitudesUiState(
                        solicitudes = reservas,
                        isLoading = false
                    )
                }
        }
    }
}
