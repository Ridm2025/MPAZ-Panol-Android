package com.example.MPAZpannol.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MPAZpannol.data.FormularioEquipo
import com.example.MPAZpannol.data.InventarioRepository
import com.example.MPAZpannol.data.validarEquipo
import com.example.MPAZpannol.data.local.CategoriaEntity
import com.example.MPAZpannol.data.local.InventarioResumen
import com.example.MPAZpannol.data.local.MPAZDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.MPAZpannol.util.RecursosNativos

data class InventarioUiState(
    val equipos: List<InventarioResumen> = emptyList(),
    val categorias: List<CategoriaEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class InventarioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = InventarioRepository(
        MPAZDatabase.getInstance(application)
    )

    var uiState by mutableStateOf(InventarioUiState())
        private set

    var formulario by mutableStateOf<FormularioEquipo?>(null)
        private set

    var errores by mutableStateOf<Map<String, String>>(
        emptyMap()
    )
        private set

    var procesando by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {

            combine(
                repository.observarInventario(),
                repository.observarCategorias()
            ) { equipos, categorias ->

                InventarioUiState(
                    equipos = equipos,
                    categorias = categorias,
                    isLoading = false
                )

            }.catch {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudo cargar el inventario"
                )
            }.collect {
                uiState = it
            }
        }
    }

    fun nuevoEquipo() {
        if (procesando) return

        errores = emptyMap()
        formulario = FormularioEquipo()
    }

    fun editarEquipo(id: Long) {
        ejecutarOperacion {

            val equipo = repository.obtenerEquipo(id)
                ?: error("Equipo no encontrado")

            errores = emptyMap()

            formulario = FormularioEquipo(
                id = equipo.id,
                nombre = equipo.nombre,
                descripcion = equipo.descripcion,
                categoriaId = equipo.categoriaId,
                stockTotal = equipo.stockTotal.toString(),
                stockOperativo = equipo.stockDisponible.toString(),
                ubicacion = equipo.ubicacion
            )
        }
    }

    fun actualizarFormulario(nuevo: FormularioEquipo) {
        if (procesando) return

        formulario = nuevo
        errores = validarEquipo(nuevo)
    }

    fun cerrarFormulario() {
        if (procesando) return

        formulario = null
        errores = emptyMap()
    }

    fun guardarEquipo(administradorId: Long) {

        if (procesando) return

        val datos = formulario ?: return

        errores = validarEquipo(datos)

        if (errores.isNotEmpty()) return

        ejecutarOperacion {

            repository.guardarEquipo(
                administradorId,
                datos
            )

            mensaje = if (datos.id == 0L) {
                "Equipo registrado correctamente"
            } else {
                "Equipo actualizado correctamente"
            }

            formulario = null
            errores = emptyMap()
        }
        RecursosNativos.vibrarConfirmacion(
            getApplication<Application>()
        )
    }

    fun cambiarEstado(
        administradorId: Long,
        equipoId: Long,
        activo: Boolean
    ) {
        ejecutarOperacion {

            repository.cambiarEstado(
                administradorId,
                equipoId,
                activo
            )

            mensaje = if (activo) {
                "Equipo reactivado correctamente"
            } else {
                "Equipo desactivado correctamente"
            }
        }
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    private fun ejecutarOperacion(
        bloque: suspend () -> Unit
    ) {
        if (procesando) return

        procesando = true

        viewModelScope.launch {
            try {
                bloque()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mensaje = e.message
                    ?: "No se pudo realizar la operación"
            } finally {
                procesando = false
            }
        }
    }
}
