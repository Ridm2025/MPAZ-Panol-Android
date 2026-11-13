package com.example.MPAZpannol.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.MPAZpannol.data.local.EquipoEntity

data class ItemCarrito(
    val equipo: EquipoEntity,
    val cantidad: Int
)

class CarritoViewModel : ViewModel() {

    var productos by mutableStateOf<List<ItemCarrito>>(
        emptyList()
    )
        private set

    val cantidadTotal: Int
        get() = productos.sumOf { it.cantidad }

    fun agregar(equipo: EquipoEntity): Boolean {

        if (!equipo.activo ||
            equipo.stockDisponible <= 0
        ) {
            return false
        }

        val existente = productos.firstOrNull {
            it.equipo.id == equipo.id
        }

        if (existente != null &&
            existente.cantidad >= equipo.stockDisponible
        ) {
            return false
        }

        productos = if (existente == null) {

            productos + ItemCarrito(
                equipo = equipo,
                cantidad = 1
            )

        } else {

            productos.map { item ->
                if (item.equipo.id == equipo.id) {
                    item.copy(
                        equipo = equipo,
                        cantidad = item.cantidad + 1
                    )
                } else {
                    item
                }
            }
        }

        return true
    }

    fun quitar(equipoId: Long) {

        productos = productos.mapNotNull { item ->

            if (item.equipo.id != equipoId) {
                item

            } else if (item.cantidad > 1) {
                item.copy(cantidad = item.cantidad - 1)

            } else {
                null
            }
        }
    }

    fun vaciar() {
        productos = emptyList()
    }
}
