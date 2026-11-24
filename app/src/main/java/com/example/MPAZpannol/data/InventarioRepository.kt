
package com.example.MPAZpannol.data

import androidx.room.withTransaction
import com.example.MPAZpannol.data.local.EquipoEntity
import com.example.MPAZpannol.data.local.MPAZDatabase

data class FormularioEquipo(
    val id: Long = 0L,
    val nombre: String = "",
    val descripcion: String = "",
    val categoriaId: Long = 0L,
    val stockTotal: String = "",
    val stockOperativo: String = ""
)

fun validarEquipo(
    formulario: FormularioEquipo
): Map<String, String> {

    val errores = mutableMapOf<String, String>()

    val total = formulario.stockTotal.toIntOrNull()
    val operativo = formulario.stockOperativo.toIntOrNull()

    if (formulario.nombre.isBlank()) {
        errores["nombre"] = "Ingresa el nombre"
    }

    if (formulario.descripcion.isBlank()) {
        errores["descripcion"] = "Ingresa una descripción"
    }

    if (formulario.categoriaId <= 0L) {
        errores["categoria"] = "Selecciona una categoría"
    }

    if (total == null || total <= 0) {
        errores["total"] = "Ingresa una cantidad mayor a cero"
    }

    if (operativo == null || operativo < 0) {
        errores["operativo"] = "Ingresa una cantidad válida"
    } else if (total != null && operativo > total) {
        errores["operativo"] =
            "No puede superar el stock total"
    }

    return errores
}

class InventarioRepository(
    private val database: MPAZDatabase
) {

    private val dao = database.catalogoDao()

    fun observarInventario() =
        dao.observarResumenInventario()

    fun observarCategorias() =
        dao.observarCategoriasAdmin()

    suspend fun obtenerEquipo(id: Long) =
        dao.buscarEquipoAdmin(id)

    private suspend fun verificarAdministrador(
        administradorId: Long
    ) {
        val rol = database.reservaDao()
            .obtenerRolUsuario(administradorId)

        require(rol == "ADMINISTRADOR") {
            "No tienes permisos de administrador"
        }
    }

    suspend fun guardarEquipo(
        administradorId: Long,
        formulario: FormularioEquipo
    ) = database.withTransaction {

        verificarAdministrador(administradorId)

        require(validarEquipo(formulario).isEmpty()) {
            "Revisa los campos del formulario"
        }

        val total = formulario.stockTotal.toInt()
        val operativo = formulario.stockOperativo.toInt()

        val categoriaExiste = dao.obtenerCategorias()
            .any { it.id == formulario.categoriaId }

        require(categoriaExiste) {
            "La categoría seleccionada no existe"
        }

        if (formulario.id == 0L) {

            dao.crearEquipoAdmin(
                EquipoEntity(
                    nombre = formulario.nombre.trim(),
                    descripcion = formulario.descripcion.trim(),
                    categoriaId = formulario.categoriaId,
                    stockTotal = total,
                    stockDisponible = operativo,
                    activo = true
                )
            )

        } else {

            val anterior = dao.buscarEquipoAdmin(formulario.id)
                ?: error("El equipo no existe")

            val compromisos = dao.contarCompromisosActivos(
                formulario.id
            )

            require(
                compromisos == 0 ||
                        operativo >= anterior.stockDisponible
            ) {
                "No puedes reducir el stock operativo mientras existan reservas o préstamos activos"
            }

            val filas = dao.editarEquipoAdmin(
                id = formulario.id,
                nombre = formulario.nombre.trim(),
                descripcion = formulario.descripcion.trim(),
                categoriaId = formulario.categoriaId,
                stockTotal = total,
                stockOperativo = operativo
            )

            check(filas == 1) {
                "No se pudo actualizar el equipo"
            }
        }
    }

    suspend fun cambiarEstado(
        administradorId: Long,
        equipoId: Long,
        activo: Boolean
    ) = database.withTransaction {

        verificarAdministrador(administradorId)

        if (!activo) {
            require(
                dao.contarCompromisosActivos(equipoId) == 0
            ) {
                "No puedes desactivar un equipo con reservas o préstamos activos"
            }
        }

        val filas = dao.cambiarEstadoEquipo(
            equipoId,
            activo
        )

        check(filas == 1) {
            "No se pudo cambiar el estado"
        }
    }
}
