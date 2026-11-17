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
import com.example.MPAZpannol.viewmodel.MisSolicitudesViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MisSolicitudesScreen(
    usuarioId: Long,
    onBackClick: () -> Unit,
    solicitudesViewModel: MisSolicitudesViewModel = viewModel()
) {
    val state = solicitudesViewModel.uiState

    LaunchedEffect(usuarioId) {
        solicitudesViewModel.cargarSolicitudes(usuarioId)
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
            text = "Mis solicitudes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        when {

            state.isLoading -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            state.error != null -> {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.solicitudes.isEmpty() -> {
                Text(
                    "Aún no tienes solicitudes registradas."
                )
            }

            else -> {

                Text(
                    "${state.solicitudes.size} solicitudes"
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
                                    text = "Estado: ${reserva.estado}",
                                    color = if (
                                        reserva.estado == "RECHAZADA"
                                    ) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )

                                HorizontalDivider()

                                Text("Sala: ${reserva.sala}")

                                Text(
                                    "Inicio: " +
                                            formatearFecha(reserva.inicioMillis)
                                )

                                Text(
                                    "Término: " +
                                            formatearFecha(reserva.finMillis)
                                )

                                Text(
                                    text = "Equipos: " +
                                            reserva.equiposResumen
                                )


                                reserva.entregaRealMillis?.let { instante ->
                                    Text(
                                        text = "Entregado el: " +
                                                formatearFecha(instante)
                                    )
                                }

                                reserva.devolucionRealMillis?.let { instante ->
                                    Text(
                                        text = "Devuelto el: " +
                                                formatearFecha(instante)
                                    )
                                }

                                if (reserva.estado == "RECHAZADA") {

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Motivo de rechazo:",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )

                                    Text(
                                        text = reserva.motivoRechazo
                                            ?.takeIf { it.isNotBlank() }
                                            ?: "No se indicó un motivo",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatearFecha(millis: Long): String {

    val formatter = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy HH:mm",
        Locale("es", "CL")
    )

    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
