package com.example.MPAZpannol.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun AdministradorHomeScreen(
    nombreUsuario: String,
    onLogout: () -> Unit
) {
    PanelBase(
        titulo = "Panel del administrador",
        nombreUsuario = nombreUsuario,
        rol = "ADMINISTRADOR",
        onLogout = onLogout
    ) {

        ModuloPendiente(
            titulo = "Gestionar solicitudes",
            descripcion =
                "Revisa, acepta o rechaza las solicitudes de equipos."
        )

        ModuloPendiente(
            titulo = "Administrar inventario",
            descripcion =
                "Gestiona los equipos y sus cantidades disponibles."
        )

        ModuloPendiente(
            titulo = "Historial de préstamos",
            descripcion =
                "Consulta las entregas, devoluciones y reservas."
        )
    }
}
