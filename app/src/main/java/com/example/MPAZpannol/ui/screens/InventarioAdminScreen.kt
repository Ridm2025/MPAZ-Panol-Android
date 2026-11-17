package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MPAZpannol.viewmodel.InventarioViewModel

@Composable
fun InventarioAdminScreen(
    onBackClick: () -> Unit,
    inventarioViewModel: InventarioViewModel = viewModel()
) {

    val state = inventarioViewModel.uiState

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
            text = "Inventario del pañol",
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
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.equipos.isEmpty() -> {
                Text("Todavía no hay equipos registrados.")
            }

            else -> {

                Text(
                    text = "Equipos registrados: " +
                            state.equipos.size
                )

                Text(
                    text = "Unidades prestadas: " +
                            state.equipos.sumOf {
                                it.unidadesPrestadas
                            }
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
                        items = state.equipos,
                        key = { it.id }
                    ) { equipo ->

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                Text(
                                    text = equipo.nombre,
                                    style = MaterialTheme
                                        .typography.titleLarge
                                )

                                Text(
                                    text = "Categoría: ${equipo.categoria}"
                                )

                                Text(
                                    text = if (equipo.activo) {
                                        "Estado: ACTIVO"
                                    } else {
                                        "Estado: INACTIVO"
                                    },
                                    color = if (equipo.activo) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                )

                                HorizontalDivider()

                                Text(
                                    "Stock total: ${equipo.stockTotal}"
                                )

                                Text(
                                    "Unidades operativas: " +
                                            equipo.stockOperativo
                                )

                                Text(
                                    "Unidades prestadas: " +
                                            equipo.unidadesPrestadas
                                )

                                Text(
                                    text = "Libres físicamente: " +
                                            equipo.libresFisicamente,
                                    color = MaterialTheme
                                        .colorScheme.primary
                                )

                                if (
                                    equipo.unidadesPrestadas >
                                    equipo.stockOperativo.toLong()
                                ) {
                                    Text(
                                        text = "Revisar inventario: " +
                                                "hay más unidades prestadas " +
                                                "que operativas.",
                                        color = MaterialTheme
                                            .colorScheme.error
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
