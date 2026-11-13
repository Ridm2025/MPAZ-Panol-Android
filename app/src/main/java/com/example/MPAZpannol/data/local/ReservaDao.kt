package com.example.MPAZpannol.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReservaDao {

    @Insert
    suspend fun insertarReserva(
        reserva: ReservaEntity
    ): Long

    @Insert
    suspend fun insertarDetalle(
        detalle: ReservaDetalleEntity
    ): Long

    @Query(
        """
        SELECT stockDisponible
        FROM equipos
        WHERE id = :equipoId
        AND activo = 1
        LIMIT 1
        """
    )
    suspend fun obtenerStockDisponible(
        equipoId: Long
    ): Int?

    @Query(
        """
        SELECT COALESCE(SUM(d.cantidad), 0)
        FROM reserva_detalles AS d
        INNER JOIN reservas AS r
            ON d.reservaId = r.id
        WHERE d.equipoId = :equipoId
        AND r.estado IN (
            'PENDIENTE',
            'ACEPTADA',
            'ENTREGADA'
        )
        AND r.inicioMillis < :finMillis
        AND r.finMillis > :inicioMillis
        """
    )
    suspend fun obtenerCantidadReservada(
        equipoId: Long,
        inicioMillis: Long,
        finMillis: Long
    ): Int
}
