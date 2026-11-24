package com.example.MPAZpannol.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.MPAZpannol.data.local.ReservaGestionAdmin
import com.example.MPAZpannol.util.RecordatoriosDevolucion
import com.example.MPAZpannol.util.RecursosNativos
import kotlinx.coroutines.delay

@Composable
fun PanelRecordatorios(
    prestamos: List<ReservaGestionAdmin>
) {

    val context = LocalContext.current

    var ahora by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        while (true) {
            ahora = System.currentTimeMillis()
            delay(30000L)
        }
    }

    val resumen = RecordatoriosDevolucion
        .obtenerResumen(prestamos, ahora)

    fun enviarNotificacion() {

        val actualizado = RecordatoriosDevolucion
            .obtenerResumen(prestamos)

        if (
            actualizado.proximas == 0 &&
            actualizado.atrasadas == 0
        ) {

            mensaje = "No hay devoluciones pendientes de aviso"
            return
        }

        val enviada = RecursosNativos.mostrarRecordatorio(
            context = context,
            titulo = "Recordatorio MPAZ Pañol",
            mensaje =
                "${actualizado.proximas} devoluciones próximas y " +
                        "${actualizado.atrasadas} devoluciones atrasadas."
        )

        mensaje = if (enviada) {
            "Notificación enviada correctamente"
        } else {
            "Las notificaciones no están disponibles o están desactivadas"
        }
    }

    val permisoNotificaciones =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { autorizado ->

            if (autorizado) {
                enviarNotificacion()
            } else {
                mensaje = "Permiso de notificaciones rechazado"
            }
        }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "Control de devoluciones",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Próximas devoluciones: " +
                        resumen.proximas,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Devoluciones atrasadas: " +
                        resumen.atrasadas,
                color = if (resumen.atrasadas > 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            AnimatedVisibility(
                visible = resumen.atrasadas > 0
            ) {

                Surface(
                    color = MaterialTheme
                        .colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {

                    Text(
                        text = "⚠ Hay préstamos que superaron su horario de devolución.",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme
                            .onErrorContainer
                    )
                }
            }

            Button(
                onClick = {

                    if (
                        Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.TIRAMISU &&
                        !RecursosNativos
                            .tienePermisoNotificaciones(context)
                    ) {

                        permisoNotificaciones.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )

                    } else {
                        enviarNotificacion()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Notificar devoluciones")
            }

            AnimatedVisibility(
                visible = mensaje.isNotEmpty()
            ) {

                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
