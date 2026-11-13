package com.example.MPAZpannol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MPAZpannol.viewmodel.CatalogoViewModel
import com.example.MPAZpannol.data.local.EquipoEntity

@Composable
fun CatalogoScreen(
    onBackClick: () -> Unit,
    onAgregarClick: (EquipoEntity) -> Unit,
    onCarritoClick: () -> Unit,
    cantidadCarrito: Int,
    catalogoViewModel: CatalogoViewModel = viewModel()
) {

    val categorias = catalogoViewModel.categorias
    val equipos = catalogoViewModel.equipos

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var categoriaSeleccionada by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val equiposFiltrados = equipos.filter { equipo ->

        val coincideBusqueda =
            equipo.nombre.contains(
                busqueda,
                ignoreCase = true
            ) ||
                    equipo.descripcion.contains(
                        busqueda,
                        ignoreCase = true
                    )

        val coincideCategoria =
            categoriaSeleccionada == null ||
                    equipo.categoriaId == categoriaSeleccionada

        coincideBusqueda && coincideCategoria
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        TextButton(onClick = onBackClick) {
            Text("← Volver")
        }

        Text(
            text = "Catálogo de equipos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCarritoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver carrito ($cantidadCarrito)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar equipo") },
            placeholder = { Text("Notebook, proyector...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = categoriaSeleccionada == null,
                onClick = {
                    categoriaSeleccionada = null
                },
                label = { Text("Todas") }
            )

            categorias.forEach { categoria ->

                FilterChip(
                    selected =
                        categoriaSeleccionada == categoria.id,
                    onClick = {
                        categoriaSeleccionada = categoria.id
                    },
                    label = {
                        Text(categoria.nombre)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {

            catalogoViewModel.isLoading -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            catalogoViewModel.error != null -> {
                Text(
                    text = catalogoViewModel.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            equiposFiltrados.isEmpty() -> {
                Text("No se encontraron equipos")
            }

            else -> {

                Text(
                    text = "${equiposFiltrados.size} equipos encontrados",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = 16.dp
                    )
                ) {

                    items(
                        items = equiposFiltrados,
                        key = { it.id }
                    ) { equipo ->

                        val categoria = categorias
                            .firstOrNull {
                                it.id == equipo.categoriaId
                            }?.nombre ?: "Sin categoría"

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
                                    text = categoria,
                                    color = MaterialTheme
                                        .colorScheme.secondary
                                )

                                Text(equipo.descripcion)

                                HorizontalDivider()

                                Text(
                                    text = "Disponibles: " +
                                            "${equipo.stockDisponible} / " +
                                            "${equipo.stockTotal}"
                                )

                                Text(
                                    text = if (
                                        equipo.stockDisponible > 0
                                    ) {
                                        "Disponible"
                                    } else {
                                        "Sin disponibilidad"
                                    },
                                    color = if (
                                        equipo.stockDisponible > 0
                                    ) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                )

                                Button(
                                    onClick = {
                                        onAgregarClick(equipo)
                                    },
                                    enabled = equipo.stockDisponible > 0,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Agregar al carrito")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
