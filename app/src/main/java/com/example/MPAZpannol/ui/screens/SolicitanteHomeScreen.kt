package com.example.MPAZpannol.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun SolicitanteHomeScreen(
    nombreUsuario: String,
    onLogout: () -> Unit
) {
    PanelBase(
        titulo = "Panel del solicitante",
        nombreUsuario = nombreUsuario,
        rol = "SOLICITANTE",
        onLogout = onLogout
    ) {

        ModuloPendiente(
            titulo = "Catálogo de equipos",
            descripcion =
                "Consulta los equipos tecnológicos disponibles en el pañol."
        )

        ModuloPendiente(
            titulo = "Nueva solicitud",
            descripcion =
                "Solicita notebooks, proyectores y otros equipos tecnológicos."
        )

        ModuloPendiente(
            titulo = "Mis solicitudes",
            descripcion =
                "Consulta el estado de tus préstamos y reservas."
        )
    }
}
