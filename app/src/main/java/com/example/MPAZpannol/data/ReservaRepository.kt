package com.example.MPAZpannol.data

import androidx.room.withTransaction
import com.example.MPAZpannol.data.local.MPAZDatabase
import com.example.MPAZpannol.data.local.ReservaEntity
import com.example.MPAZpannol.data.local.ReservaDetalleEntity
import com.example.MPAZpannol.viewmodel.ItemCarrito

class ReservaRepository(
    private val database: MPAZDatabase
) {
    private val dao = database.reservaDao()

    suspend fun crearReserva(
        usuarioId: Long,
        sala: String,
        inicioMillis: Long,
        finMillis: Long,
        productos: List<ItemCarrito>
    ): Long = database.withTransaction {

        val ahora = System.currentTimeMillis()
        val tresHoras = 3L * 60 * 60 * 1000

        require(usuarioId > 0) {
            "Debes iniciar sesión nuevamente"
        }

        require(sala.isNotBlank()) {
            "Debes indicar una sala"
        }

        require(inicioMillis - ahora >= tresHoras) {
            "La reserva requiere 3 horas de anticipación"
        }

        require(finMillis > inicioMillis) {
            "El horario de término debe ser posterior"
        }

        require(productos.isNotEmpty()) {
            "El carrito está vacío"
        }

        require(
            productos.map { it.equipo.id }.distinct().size ==
                    productos.size
        ) {
            "Hay equipos duplicados en el carrito"
        }

        for (item in productos) {

            require(item.cantidad > 0) {
                "Cantidad de equipo inválida"
            }

            val disponible = dao.obtenerStockDisponible(
                item.equipo.id
            ) ?: 0

            val reservado = dao.obtenerCantidadReservada(
                equipoId = item.equipo.id,
                inicioMillis = inicioMillis,
                finMillis = finMillis
            )

            require(
                item.cantidad <= disponible - reservado
            ) {
                "No hay suficientes unidades de " +
                        item.equipo.nombre +
                        " para ese horario"
            }
        }

        val reservaId = dao.insertarReserva(
            ReservaEntity(
                usuarioId = usuarioId,
                sala = sala.trim(),
                inicioMillis = inicioMillis,
                finMillis = finMillis,
                estado = "PENDIENTE",
                creadaMillis = ahora
            )
        )

        productos.forEach { item ->
            dao.insertarDetalle(
                ReservaDetalleEntity(
                    reservaId = reservaId,
                    equipoId = item.equipo.id,
                    cantidad = item.cantidad
                )
            )
        }

        reservaId
    }
}
