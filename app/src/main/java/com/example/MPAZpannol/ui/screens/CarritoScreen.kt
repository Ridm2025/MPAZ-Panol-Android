package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.MPAZpannol.data.local.EquipoEntity
import com.example.MPAZpannol.viewmodel.ItemCarrito

@Composable
fun CarritoScreen(
    productos: List<ItemCarrito>,
    onBackClick: () -> Unit,
    onAgregarClick: (EquipoEntity) -> Unit,
    onQuitarClick: (Long) -> Unit,
    onVaciarClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        TextButton(onClick = onBackClick) {
            Text("← Volver al catálogo")
        }

        Text(
            text = "Mi carrito",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (productos.isEmpty()) {

            Text("Todavía no has agregado equipos.")

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = productos,
                    key = { it.equipo.id }
                ) { item ->

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = item.equipo.nombre,
                                style = MaterialTheme
                                    .typography.titleMedium
                            )

                            Text(item.equipo.descripcion)

                            Text(
                                "Disponibles: " +
                                        item.equipo.stockDisponible
                            )

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {

                                OutlinedButton(
                                    onClick = {
                                        onQuitarClick(
                                            item.equipo.id
                                        )
                                    }
                                ) {
                                    Text("−")
                                }

                                Text(
                                    text = item.cantidad.toString(),
                                    style = MaterialTheme
                                        .typography.titleMedium
                                )

                                OutlinedButton(
                                    onClick = {
                                        onAgregarClick(
                                            item.equipo
                                        )
                                    },
                                    enabled =
                                        item.cantidad <
                                                item.equipo.stockDisponible
                                ) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total de unidades: " +
                        productos.sumOf { it.cantidad },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onVaciarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vaciar carrito")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Siguiente etapa: elegir fecha, " +
                        "horario y sala para solicitar el préstamo.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
