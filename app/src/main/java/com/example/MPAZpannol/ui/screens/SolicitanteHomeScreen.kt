package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SolicitanteHomeScreen(
    nombreUsuario: String,
    onLogout: () -> Unit,
    onCatalogoClick: () -> Unit
) {

    PanelBase(
        titulo = "Panel del solicitante",
        nombreUsuario = nombreUsuario,
        rol = "SOLICITANTE",
        onLogout = onLogout
    ) {

        Button(
            onClick = onCatalogoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver catálogo de equipos")
        }

        ModuloPendiente(
            titulo = "Nueva solicitud",
            descripcion =
                "Solicita notebooks, proyectores y otros equipos."
        )

        ModuloPendiente(
            titulo = "Mis solicitudes",
            descripcion =
                "Consulta el estado de tus préstamos."
        )
    }
}
