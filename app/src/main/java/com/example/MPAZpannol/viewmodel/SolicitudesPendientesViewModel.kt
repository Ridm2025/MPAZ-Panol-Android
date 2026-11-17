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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    var procesandoId by mutableStateOf<Long?>(null)
        private set

    var mensajeAccion by mutableStateOf<String?>(null)
        private set

    var errorAccion by mutableStateOf<String?>(null)
        private set

    fun aceptar(
        administradorId: Long,
        reservaId: Long
    ) {
        cambiarEstado(
            administradorId,
            reservaId,
            aprobar = true
        )
    }

    fun rechazar(
        administradorId: Long,
        reservaId: Long,
        motivo: String
    ) {
        cambiarEstado(
            administradorId,
            reservaId,
            aprobar = false,
            motivo = motivo
        )
    }

    private fun cambiarEstado(
        administradorId: Long,
        reservaId: Long,
        aprobar: Boolean,
        motivo: String = ""
    ) {

        if (procesandoId != null) return

        procesandoId = reservaId
        mensajeAccion = null
        errorAccion = null

        viewModelScope.launch {

            try {
                withContext(Dispatchers.IO) {
                    repository.resolverSolicitud(
                        administradorId = administradorId,
                        reservaId = reservaId,
                        aprobar = aprobar,
                        motivoRechazo = motivo
                    )
                }

                mensajeAccion = if (aprobar) {
                    "Solicitud #$reservaId aceptada"
                } else {
                    "Solicitud #$reservaId rechazada"
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: IllegalArgumentException) {
                errorAccion = e.message

            } catch (e: IllegalStateException) {
                errorAccion = e.message

            } catch (e: Exception) {
                errorAccion =
                    "Ocurrió un error al procesar la solicitud"

            } finally {
                procesandoId = null
            }
        }
    }

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
