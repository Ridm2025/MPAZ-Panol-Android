package com.example.MPAZpannol.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MPAZpannol.data.FormularioEquipo
import com.example.MPAZpannol.data.local.CategoriaEntity
import com.example.MPAZpannol.data.local.InventarioResumen
import com.example.MPAZpannol.viewmodel.InventarioViewModel

@Composable
fun InventarioAdminScreen(
    administradorId: Long,
    onBackClick: () -> Unit,
    inventarioViewModel: InventarioViewModel = viewModel()
) {

    val vm = inventarioViewModel
    val state = vm.uiState

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var equipoConfirmacion by remember {
        mutableStateOf<InventarioResumen?>(null)
    }

    LaunchedEffect(vm.mensaje) {
        vm.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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

            Button(
                onClick = vm::nuevoEquipo,
                modifier = Modifier.fillMaxWidth(),
                enabled = !vm.procesando && !state.isLoading
            ) {
                Text("+ Agregar equipo")
            }

            Text(
                "Equipos registrados: ${state.equipos.size}"
            )

            Text(
                "Unidades prestadas: " +
                        state.equipos.sumOf {
                            it.unidadesPrestadas
                        }
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
                    Text("No hay equipos registrados")
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = state.equipos,
                            key = { it.id }
                        ) { equipo ->

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
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
                                        "Categoría: ${equipo.categoria}"
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
                                            "Revisar inconsistencia de stock",
                                            color = MaterialTheme
                                                .colorScheme.error
                                        )
                                    }

                                    HorizontalDivider()

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement =
                                            Arrangement.spacedBy(8.dp)
                                    ) {

                                        OutlinedButton(
                                            onClick = {
                                                vm.editarEquipo(equipo.id)
                                            },
                                            enabled = !vm.procesando,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Editar")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                equipoConfirmacion = equipo
                                            },
                                            enabled = !vm.procesando,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                if (equipo.activo) {
                                                    "Desactivar"
                                                } else {
                                                    "Reactivar"
                                                },
                                                color = if (equipo.activo) {
                                                    MaterialTheme
                                                        .colorScheme.error
                                                } else {
                                                    MaterialTheme
                                                        .colorScheme.primary
                                                }
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
    }

    vm.formulario?.let { formulario ->

        FormularioEquipoDialog(
            formulario = formulario,
            categorias = state.categorias,
            errores = vm.errores,
            procesando = vm.procesando,
            onCambio = vm::actualizarFormulario,
            onCancelar = vm::cerrarFormulario,
            onGuardar = {
                vm.guardarEquipo(administradorId)
            }
        )
    }

    equipoConfirmacion?.let { equipo ->

        AlertDialog(
            onDismissRequest = {
                equipoConfirmacion = null
            },
            title = {
                Text(
                    if (equipo.activo) {
                        "Desactivar equipo"
                    } else {
                        "Reactivar equipo"
                    }
                )
            },
            text = {
                Text(
                    if (equipo.activo) {
                        "${equipo.nombre} dejará de aparecer en el catálogo. Su historial se conservará."
                    } else {
                        "${equipo.nombre} volverá a estar disponible en el catálogo."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.cambiarEstado(
                            administradorId,
                            equipo.id,
                            !equipo.activo
                        )

                        equipoConfirmacion = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        equipoConfirmacion = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FormularioEquipoDialog(
    formulario: FormularioEquipo,
    categorias: List<CategoriaEntity>,
    errores: Map<String, String>,
    procesando: Boolean,
    onCambio: (FormularioEquipo) -> Unit,
    onCancelar: () -> Unit,
    onGuardar: () -> Unit
) {

    var menuCategorias by remember {
        mutableStateOf(false)
    }

    val categoriaSeleccionada = categorias.find {
        it.id == formulario.categoriaId
    }

    AlertDialog(
        onDismissRequest = {
            if (!procesando) onCancelar()
        },
        title = {
            Text(
                if (formulario.id == 0L) {
                    "Nuevo equipo"
                } else {
                    "Editar equipo"
                }
            )
        },
        text = {

            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                CampoEquipo(
                    valor = formulario.nombre,
                    etiqueta = "Nombre del equipo",
                    error = errores["nombre"],
                    habilitado = !procesando,
                    onCambio = {
                        onCambio(formulario.copy(nombre = it))
                    }
                )

                CampoEquipo(
                    valor = formulario.descripcion,
                    etiqueta = "Descripción",
                    error = errores["descripcion"],
                    habilitado = !procesando,
                    onCambio = {
                        onCambio(
                            formulario.copy(descripcion = it)
                        )
                    }
                )

                Box {

                    OutlinedButton(
                        onClick = {
                            menuCategorias = true
                        },
                        enabled = !procesando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            categoriaSeleccionada?.nombre
                                ?: "Seleccionar categoría"
                        )
                    }

                    DropdownMenu(
                        expanded = menuCategorias,
                        onDismissRequest = {
                            menuCategorias = false
                        }
                    ) {

                        categorias.forEach { categoria ->

                            DropdownMenuItem(
                                text = {
                                    Text(categoria.nombre)
                                },
                                onClick = {
                                    onCambio(
                                        formulario.copy(
                                            categoriaId = categoria.id
                                        )
                                    )

                                    menuCategorias = false
                                }
                            )
                        }
                    }
                }

                errores["categoria"]?.let {
                    Text(
                        text = "⚠ $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                CampoEquipo(
                    valor = formulario.stockTotal,
                    etiqueta = "Stock total",
                    error = errores["total"],
                    numerico = true,
                    habilitado = !procesando,
                    onCambio = {
                        onCambio(
                            formulario.copy(stockTotal = it)
                        )
                    }
                )

                CampoEquipo(
                    valor = formulario.stockOperativo,
                    etiqueta = "Unidades operativas",
                    error = errores["operativo"],
                    numerico = true,
                    habilitado = !procesando,
                    onCambio = {
                        onCambio(
                            formulario.copy(stockOperativo = it)
                        )
                    }
                )

                if (procesando) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onGuardar,
                enabled = !procesando
            ) {
                Text(
                    if (procesando) {
                        "Guardando..."
                    } else {
                        "Guardar"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelar,
                enabled = !procesando
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CampoEquipo(
    valor: String,
    etiqueta: String,
    error: String?,
    numerico: Boolean = false,
    habilitado: Boolean = true,
    onCambio: (String) -> Unit
) {

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = {
            Text(etiqueta)
        },
        enabled = habilitado,
        isError = error != null,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numerico) {
                KeyboardType.Number
            } else {
                KeyboardType.Text
            }
        ),
        supportingText = {
            if (error != null) {
                Text("⚠ $error")
            }
        },
        trailingIcon = {
            if (error != null) {
                Text(
                    "!",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}
