package com.example.MPAZpannol.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MPAZpannol.viewmodel.ItemCarrito
import com.example.MPAZpannol.viewmodel.ReservaViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@Composable
fun ReservaScreen(
    usuarioId: Long,
    productos: List<ItemCarrito>,
    onBackClick: () -> Unit,
    onReservaCreada: (Long) -> Unit,
    reservaViewModel: ReservaViewModel = viewModel()
) {
    val state = reservaViewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(state.reservaCreadaId) {
        state.reservaCreadaId?.let {
            onReservaCreada(it)
        }
    }

    fun seleccionarHora(esInicio: Boolean) {
        val ahora = LocalTime.now()

        TimePickerDialog(
            context,
            { _, hora, minuto ->
                val valor = String.format(
                    Locale.ROOT,
                    "%02d:%02d",
                    hora,
                    minuto
                )

                if (esInicio) {
                    reservaViewModel.onHoraInicioChanged(valor)
                } else {
                    reservaViewModel.onHoraFinChanged(valor)
                }
            },
            ahora.hour,
            ahora.minute,
            true
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        TextButton(
            onClick = onBackClick,
            enabled = !state.isSaving
        ) {
            Text("← Volver al carrito")
        }

        Text(
            text = "Solicitar préstamo",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Equipos seleccionados: " +
                    productos.sumOf { it.cantidad }
        )

        HorizontalDivider()

        Text("Fecha del préstamo")

        OutlinedButton(
            onClick = {
                val hoy = LocalDate.now()

                DatePickerDialog(
                    context,
                    { _, anio, mes, dia ->
                        reservaViewModel.onFechaChanged(
                            String.format(
                                Locale.ROOT,
                                "%04d-%02d-%02d",
                                anio,
                                mes + 1,
                                dia
                            )
                        )
                    },
                    hoy.year,
                    hoy.monthValue - 1,
                    hoy.dayOfMonth
                ).show()
            },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.fecha.isEmpty()) {
                    "Seleccionar fecha"
                } else {
                    state.fecha
                }
            )
        }

        state.fechaError?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text("Hora de inicio")

        OutlinedButton(
            onClick = { seleccionarHora(true) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                state.horaInicio.ifEmpty {
                    "Seleccionar hora de inicio"
                }
            )
        }

        state.inicioError?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text("Hora de término")

        OutlinedButton(
            onClick = { seleccionarHora(false) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                state.horaFin.ifEmpty {
                    "Seleccionar hora de término"
                }
            )
        }

        state.finError?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = state.sala,
            onValueChange = reservaViewModel::onSalaChanged,
            label = { Text("Sala de uso") },
            placeholder = { Text("Ejemplo: B204") },
            isError = state.salaError != null,
            supportingText = {
                state.salaError?.let { Text(it) }
            },
            singleLine = true,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Las reservas deben solicitarse " +
                    "con un mínimo de 3 horas de anticipación.",
            style = MaterialTheme.typography.bodySmall
        )

        state.errorGeneral?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                reservaViewModel.confirmarReserva(
                    usuarioId,
                    productos
                )
            },
            enabled = !state.isSaving &&
                    productos.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Confirmar solicitud")
            }
        }
    }
}
