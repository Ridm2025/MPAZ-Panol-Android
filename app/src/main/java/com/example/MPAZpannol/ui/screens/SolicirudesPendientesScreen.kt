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
import com.example.MPAZpannol.viewmodel.SolicitudesPendientesViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SolicitudesPendientesScreen(
    administradorId: Long,
    onBackClick: () -> Unit,
    solicitudesViewModel: SolicitudesPendientesViewModel = viewModel()
) {

    val state = solicitudesViewModel.uiState

    var reservaARechazar by remember {
        mutableStateOf<Long?>(null)
    }

    var motivo by remember {
        mutableStateOf("")
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
            text = "Solicitudes pendientes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        solicitudesViewModel.mensajeAccion?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary
            )
        }

        solicitudesViewModel.errorAccion?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        when {

            state.isLoading -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            state.error != null -> {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.solicitudes.isEmpty() -> {
                Text("No hay solicitudes pendientes.")
            }

            else -> {

                Text(
                    text = "${state.solicitudes.size} solicitudes pendientes"
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = 16.dp
                    )
                ) {

                    items(
                        items = state.solicitudes,
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

                                Text(
                                    text = reserva.estado,
                                    color = MaterialTheme
                                        .colorScheme.secondary
                                )

                                HorizontalDivider()

                                Text(
                                    "Solicitante: " +
                                            reserva.nombreSolicitante
                                )

                                Text(
                                    "Correo: " +
                                            reserva.correoSolicitante
                                )

                                Text(
                                    "Sala: ${reserva.sala}"
                                )

                                Text(
                                    "Inicio: " +
                                            formatearFechaAdmin(
                                                reserva.inicioMillis
                                            )
                                )

                                Text(
                                    "Término: " +
                                            formatearFechaAdmin(
                                                reserva.finMillis
                                            )
                                )

                                Text(
                                    "Equipos: " +
                                            reserva.equiposResumen
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {

                                    Button(
                                        onClick = {
                                            solicitudesViewModel.aceptar(
                                                administradorId,
                                                reserva.id
                                            )
                                        },
                                        enabled =
                                            solicitudesViewModel.procesandoId == null,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Aceptar")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            reservaARechazar = reserva.id
                                            motivo = ""
                                        },
                                        enabled =
                                            solicitudesViewModel.procesandoId == null,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Rechazar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (reservaARechazar != null) {

        AlertDialog(
            onDismissRequest = {
                reservaARechazar = null
            },
            title = {
                Text("Rechazar solicitud")
            },
            text = {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Indica por qué no se puede aprobar " +
                                "esta solicitud."
                    )

                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = {
                            Text("Motivo del rechazo")
                        },
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = reservaARechazar

                        if (id != null) {
                            solicitudesViewModel.rechazar(
                                administradorId,
                                id,
                                motivo
                            )

                            reservaARechazar = null
                            motivo = ""
                        }
                    },
                    enabled = motivo.trim().length >= 5
                ) {
                    Text("Confirmar rechazo")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        reservaARechazar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatearFechaAdmin(
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
