package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale


@Composable
fun HomeScreen(
    nombreUsuario: String,
    rolUsuario: String,
    onLogout: () -> Unit,
    onCatalogoClick: () -> Unit
) {
    when (rolUsuario.trim().uppercase(Locale.ROOT)) {

        "SOLICITANTE" -> {
            SolicitanteHomeScreen(
                nombreUsuario = nombreUsuario,
                onLogout = onLogout,
                onCatalogoClick = onCatalogoClick
            )
        }

        "ADMINISTRADOR" -> {
            AdministradorHomeScreen(
                nombreUsuario = nombreUsuario,
                onLogout = onLogout
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {
                Text("Rol de usuario no reconocido")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onLogout) {
                    Text("Volver a bienvenida")
                }
            }
        }
    }
}

@Composable
internal fun PanelBase(
    titulo: String,
    nombreUsuario: String,
    rol: String,
    onLogout: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    var mostrarPerfil by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "MPAZ Pañol",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "¡Hola, $nombreUsuario!",
            style = MaterialTheme.typography.titleMedium
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Rol: $rol",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        contenido()

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { mostrarPerfil = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mi perfil")
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }

    if (mostrarPerfil) {
        AlertDialog(
            onDismissRequest = {
                mostrarPerfil = false
            },
            title = {
                Text("Mi perfil")
            },
            text = {
                Text(
                    "Nombre: $nombreUsuario\nRol: $rol"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarPerfil = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
internal fun ModuloPendiente(
    titulo: String,
    descripcion: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "En preparación",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
