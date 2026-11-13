package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.ReservaRepository
import com.example.MPAZpannol.data.local.MPAZDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class ReservaUiState(
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val sala: String = "",
    val fechaError: String? = null,
    val inicioError: String? = null,
    val finError: String? = null,
    val salaError: String? = null,
    val errorGeneral: String? = null,
    val isSaving: Boolean = false,
    val reservaCreadaId: Long? = null
)

class ReservaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        MPAZDatabase.getInstance(application)
    )

    var uiState by mutableStateOf(ReservaUiState())
        private set

    fun onFechaChanged(value: String) {
        uiState = uiState.copy(
            fecha = value,
            fechaError = null,
            inicioError = null
        )
    }

    fun onHoraInicioChanged(value: String) {
        uiState = uiState.copy(
            horaInicio = value,
            inicioError = null
        )
    }

    fun onHoraFinChanged(value: String) {
        uiState = uiState.copy(
            horaFin = value,
            finError = null
        )
    }

    fun onSalaChanged(value: String) {
        uiState = uiState.copy(
            sala = value,
            salaError = null
        )
    }

    fun confirmarReserva(
        usuarioId: Long,
        productos: List<ItemCarrito>
    ) {
        if (uiState.isSaving ||
            uiState.reservaCreadaId != null
        ) return

        val fecha = runCatching {
            LocalDate.parse(uiState.fecha)
        }.getOrNull()

        val horaInicio = runCatching {
            LocalTime.parse(uiState.horaInicio)
        }.getOrNull()

        val horaFin = runCatching {
            LocalTime.parse(uiState.horaFin)
        }.getOrNull()

        val inicioMillis =
            if (fecha != null && horaInicio != null) {
                convertirAMillis(fecha, horaInicio)
            } else null

        val finMillis =
            if (fecha != null && horaFin != null) {
                convertirAMillis(fecha, horaFin)
            } else null

        val fechaError =
            if (fecha == null) "Selecciona una fecha"
            else null

        var inicioError =
            if (horaInicio == null) {
                "Selecciona la hora de inicio"
            } else null

        var finError =
            if (horaFin == null) {
                "Selecciona la hora de término"
            } else null

        val salaError =
            if (uiState.sala.trim().length < 2) {
                "Ingresa una sala válida"
            } else null

        if (inicioMillis != null &&
            inicioMillis - System.currentTimeMillis() <
            3L * 60 * 60 * 1000
        ) {
            inicioError =
                "Debes reservar con al menos 3 horas"
        }

        if (inicioMillis != null &&
            finMillis != null &&
            finMillis <= inicioMillis
        ) {
            finError =
                "El término debe ser posterior al inicio"
        }

        uiState = uiState.copy(
            fechaError = fechaError,
            inicioError = inicioError,
            finError = finError,
            salaError = salaError,
            errorGeneral = null
        )

        if (fechaError != null ||
            inicioError != null ||
            finError != null ||
            salaError != null
        ) return

        if (usuarioId <= 0 || productos.isEmpty()) {
            uiState = uiState.copy(
                errorGeneral =
                    "Inicia sesión y selecciona equipos"
            )
            return
        }

        uiState = uiState.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val id = withContext(Dispatchers.IO) {
                    repository.crearReserva(
                        usuarioId = usuarioId,
                        sala = uiState.sala.trim(),
                        inicioMillis = requireNotNull(inicioMillis),
                        finMillis = requireNotNull(finMillis),
                        productos = productos
                    )
                }

                uiState = uiState.copy(
                    isSaving = false,
                    reservaCreadaId = id
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: IllegalArgumentException) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorGeneral = e.message
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorGeneral =
                        "No se pudo guardar la reserva"
                )
            }
        }
    }

    private fun convertirAMillis(
        fecha: LocalDate,
        hora: LocalTime
    ): Long {
        return LocalDateTime.of(fecha, hora)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
