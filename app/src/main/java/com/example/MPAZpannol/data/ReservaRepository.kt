package com.example.MPAZpannol.data

import androidx.room.withTransaction
import com.example.MPAZpannol.data.local.MPAZDatabase
import com.example.MPAZpannol.data.local.ReservaEntity
import com.example.MPAZpannol.data.local.ReservaDetalleEntity
import com.example.MPAZpannol.viewmodel.ItemCarrito
import com.example.MPAZpannol.data.local.ReservaResumen
import kotlinx.coroutines.flow.Flow
import com.example.MPAZpannol.data.local.ReservaPendienteAdmin
import com.example.MPAZpannol.data.local.ReservaGestionAdmin

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

    fun observarReservasDeUsuario(
        usuarioId: Long
    ): Flow<List<ReservaResumen>> {

        return dao.observarReservasDeUsuario(usuarioId)
    }

    fun observarReservasPendientes():
            Flow<List<ReservaPendienteAdmin>> {

        return dao.observarReservasPendientes()
    }

    suspend fun resolverSolicitud(
        administradorId: Long,
        reservaId: Long,
        aprobar: Boolean,
        motivoRechazo: String = ""
    ) {
        database.withTransaction {

            val rol = dao.obtenerRolUsuario(administradorId)

            require(rol == "ADMINISTRADOR") {
                "No tienes permisos de administrador"
            }

            val reserva = dao.obtenerReservaPorId(reservaId)
                ?: throw IllegalArgumentException(
                    "La solicitud no existe"
                )

            require(reserva.estado == "PENDIENTE") {
                "Esta solicitud ya fue procesada"
            }

            val nuevoEstado: String
            val motivo: String?

            if (aprobar) {

                require(
                    reserva.inicioMillis > System.currentTimeMillis()
                ) {
                    "No puedes aceptar una reserva que ya comenzó"
                }

                val detalles = dao.obtenerDetalles(reservaId)

                require(detalles.isNotEmpty()) {
                    "La solicitud no tiene equipos"
                }

                for (detalle in detalles) {

                    val stock = dao.obtenerStockDisponible(
                        detalle.equipoId
                    ) ?: 0

                    val comprometidas =
                        dao.obtenerCantidadComprometida(
                            equipoId = detalle.equipoId,
                            inicioMillis = reserva.inicioMillis,
                            finMillis = reserva.finMillis
                        )

                    require(
                        detalle.cantidad > 0 &&
                                detalle.cantidad <= stock - comprometidas
                    ) {
                        "No hay stock suficiente para aceptar " +
                                "la solicitud"
                    }
                }

                nuevoEstado = "ACEPTADA"
                motivo = null

            } else {

                require(motivoRechazo.trim().length >= 5) {
                    "Debes indicar un motivo de rechazo"
                }

                nuevoEstado = "RECHAZADA"
                motivo = motivoRechazo.trim()
            }

            val filasActualizadas =
                dao.actualizarEstadoPendiente(
                    reservaId = reservaId,
                    nuevoEstado = nuevoEstado,
                    motivoRechazo = motivo
                )

            check(filasActualizadas == 1) {
                "No fue posible actualizar la solicitud"
            }
        }
    }

    fun observarPrestamosEnGestion():
            Flow<List<ReservaGestionAdmin>> {

        return dao.observarPrestamosEnGestion()
    }

    suspend fun avanzarEstadoPrestamo(
        administradorId: Long,
        reservaId: Long
    ): String = database.withTransaction {

        val rol = dao.obtenerRolUsuario(administradorId)

        require(rol == "ADMINISTRADOR") {
            "No tienes permisos de administrador"
        }

        val reserva = dao.obtenerReservaPorId(reservaId)
            ?: throw IllegalArgumentException(
                "No se encontró el préstamo"
            )

        val instanteActual = System.currentTimeMillis()

        val nuevoEstado: String
        val filasActualizadas: Int

        when (reserva.estado) {

            "ACEPTADA" -> {

                val detalles = dao.obtenerDetalles(reservaId)

                require(detalles.isNotEmpty()) {
                    "La reserva no contiene equipos"
                }

                for (detalle in detalles) {

                    val capacidadOperativa =
                        dao.obtenerStockDisponible(
                            detalle.equipoId
                        ) ?: 0

                    val cantidadFuera =
                        dao.obtenerCantidadEntregada(
                            detalle.equipoId
                        )

                    require(
                        detalle.cantidad > 0 &&
                                detalle.cantidad.toLong() +
                                cantidadFuera.toLong() <=
                                capacidadOperativa.toLong()
                    ) {
                        "No hay unidades físicas suficientes " +
                                "para realizar la entrega"
                    }
                }

                filasActualizadas =
                    dao.registrarEntregaReal(
                        reservaId = reservaId,
                        instante = instanteActual
                    )

                nuevoEstado = "ENTREGADA"
            }

            "ENTREGADA" -> {

                filasActualizadas =
                    dao.registrarDevolucionReal(
                        reservaId = reservaId,
                        instante = instanteActual
                    )

                nuevoEstado = "DEVUELTA"
            }

            "DEVUELTA" -> {

                filasActualizadas =
                    dao.actualizarEstadoPrestamo(
                        reservaId = reservaId,
                        estadoAnterior = "DEVUELTA",
                        nuevoEstado = "FINALIZADA"
                    )

                nuevoEstado = "FINALIZADA"
            }

            else -> {
                throw IllegalArgumentException(
                    "No se puede avanzar desde ${reserva.estado}"
                )
            }
        }

        check(filasActualizadas == 1) {
            "No fue posible actualizar el préstamo"
        }

        nuevoEstado
    }
}
