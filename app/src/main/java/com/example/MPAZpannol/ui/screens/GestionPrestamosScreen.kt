package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MPAZpannol.data.local.ReservaGestionAdmin
import com.example.MPAZpannol.viewmodel.GestionPrestamosViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.MPAZpannol.ui.components.PanelRecordatorios

@Composable
fun GestionPrestamosScreen(
    administradorId: Long,
    onBackClick: () -> Unit,
    gestionViewModel: GestionPrestamosViewModel = viewModel()
) {
    val state = gestionViewModel.uiState

    var reservaAConfirmar by remember {
        mutableStateOf<ReservaGestionAdmin?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onBackClick) {
            Text("← Volver")
        }

        Text(
            text = "Gestión de préstamos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        state.mensaje?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )

        } else if (state.prestamos.isEmpty()) {
            Text("No hay préstamos pendientes de entrega o cierre.")

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PanelRecordatorios(
                        prestamos = gestionViewModel.uiState.prestamos
                    )
                }
                items(
                    items = state.prestamos,
                    key = { it.id }
                ) { reserva ->

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "Solicitud #${reserva.id}",
                                style = MaterialTheme
                                    .typography.titleLarge
                            )

                            Text("Estado: ${reserva.estado}")

                            HorizontalDivider()

                            Text(
                                "Solicitante: " +
                                        reserva.nombreSolicitante
                            )

                            Text(
                                "Correo: " +
                                        reserva.correoSolicitante
                            )

                            Text("Sala: ${reserva.sala}")

                            Text(
                                "Inicio: " +
                                        formatearFechaPrestamo(
                                            reserva.inicioMillis
                                        )
                            )

                            Text(
                                "Término: " +
                                        formatearFechaPrestamo(
                                            reserva.finMillis
                                        )
                            )

                            Text(
                                "Equipos: ${reserva.equiposResumen}"
                            )

                            reserva.entregaRealMillis?.let { instante ->
                                Text(
                                    text = "Entrega real: " +
                                            formatearFechaPrestamo(instante)
                                )
                            }

                            reserva.devolucionRealMillis?.let { instante ->
                                Text(
                                    text = "Devolución real: " +
                                            formatearFechaPrestamo(instante)
                                )
                            }

                            val accion = when (reserva.estado) {
                                "ACEPTADA" -> "Registrar entrega"
                                "ENTREGADA" -> "Registrar devolución"
                                "DEVUELTA" -> "Finalizar préstamo"
                                else -> null
                            }

                            if (accion != null) {
                                Button(
                                    onClick = {
                                        reservaAConfirmar = reserva
                                    },
                                    enabled =
                                        gestionViewModel.procesandoId == null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(accion)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val seleccionada = reservaAConfirmar

    if (seleccionada != null) {

        val accion = when (seleccionada.estado) {
            "ACEPTADA" -> "la entrega"
            "ENTREGADA" -> "la devolución"
            else -> "la finalización"
        }

        AlertDialog(
            onDismissRequest = {
                reservaAConfirmar = null
            },
            title = {
                Text("Confirmar operación")
            },
            text = {
                Text(
                    "¿Deseas registrar $accion de la " +
                            "solicitud #${seleccionada.id}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        gestionViewModel.avanzarEstado(
                            administradorId,
                            seleccionada.id
                        )
                        reservaAConfirmar = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        reservaAConfirmar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatearFechaPrestamo(
    millis: Long
): String {
    val formatter = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy HH:mm",
        Locale.forLanguageTag("es-CL")
    )

    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
