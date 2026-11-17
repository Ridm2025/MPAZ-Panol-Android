package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AdministradorHomeScreen(
    nombreUsuario: String,
    onLogout: () -> Unit,
    onPendientesClick: () -> Unit,
    onGestionPrestamosClick: () -> Unit,
    onInventarioClick: () -> Unit
) {

    PanelBase(
        titulo = "Panel del administrador",
        nombreUsuario = nombreUsuario,
        rol = "ADMINISTRADOR",
        onLogout = onLogout
    ) {

        Button(
            onClick = onPendientesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Solicitudes pendientes")
        }

        Button(
            onClick = onGestionPrestamosClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gestionar entregas y devoluciones")
        }

        Button(
            onClick = onInventarioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Administrar inventario")
        }

        ModuloPendiente(
            titulo = "Historial de préstamos",
            descripcion =
                "Consulta entregas y devoluciones."
        )
    }
}
