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
    onPendientesClick: () -> Unit
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

        ModuloPendiente(
            titulo = "Administrar inventario",
            descripcion =
                "Gestiona los equipos y las cantidades disponibles."
        )

        ModuloPendiente(
            titulo = "Historial de préstamos",
            descripcion =
                "Consulta entregas y devoluciones."
        )
    }
}
