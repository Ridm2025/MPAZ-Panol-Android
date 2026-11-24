package com.example.MPAZpannol.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.MPAZpannol.util.RecursosNativos
import com.example.MPAZpannol.worker.ProgramadorRecordatorios

@Composable
fun ControlAvisosAutomaticos(
    administradorId: Long
) {

    val context = LocalContext.current

    var activo by remember(administradorId) {

        mutableStateOf(
            ProgramadorRecordatorios
                .administradorActivo(context)
                    == administradorId
        )
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    fun activarRecordatorios() {

        try {

            ProgramadorRecordatorios.activar(
                context,
                administradorId
            )

            activo = true
            mensaje = "Recordatorios configurados"

        } catch (e: Exception) {

            mensaje =
                "No se pudieron programar los recordatorios"
        }
    }

    val permiso =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { autorizado ->

            if (autorizado) {

                activarRecordatorios()

            } else {

                mensaje =
                    "Debes autorizar las notificaciones"
            }
        }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "Recordatorios automáticos",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (activo) {
                    "Estado: ACTIVADO"
                } else {
                    "Estado: DESACTIVADO"
                },
                color = if (activo) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Text(
                "Revisión periódica de las devoluciones " +
                        "próximas y atrasadas."
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    if (activo) {

                        ProgramadorRecordatorios
                            .desactivar(context)

                        activo = false

                        mensaje =
                            "Recordatorios desactivados"

                    } else {

                        if (
                            Build.VERSION.SDK_INT >=
                            Build.VERSION_CODES.TIRAMISU &&
                            !RecursosNativos
                                .tienePermisoNotificaciones(
                                    context
                                )
                        ) {

                            permiso.launch(
                                Manifest.permission
                                    .POST_NOTIFICATIONS
                            )

                        } else {

                            activarRecordatorios()
                        }
                    }
                }
            ) {

                Text(
                    if (activo) {
                        "Desactivar recordatorios"
                    } else {
                        "Activar recordatorios"
                    }
                )
            }

            if (mensaje.isNotEmpty()) {

                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
