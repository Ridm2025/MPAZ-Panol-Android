package com.example.MPAZpannol.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

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

    @Query(
        """
    SELECT
        r.id AS id,
        r.sala AS sala,
        r.inicioMillis AS inicioMillis,
        r.finMillis AS finMillis,
        r.estado AS estado,
        COALESCE(
            GROUP_CONCAT(
                e.nombre || ' x' || d.cantidad,
                ', '
            ),
            'Sin equipos'
        ) AS equiposResumen
    FROM reservas AS r
    LEFT JOIN reserva_detalles AS d
        ON r.id = d.reservaId
    LEFT JOIN equipos AS e
        ON d.equipoId = e.id
    WHERE r.usuarioId = :usuarioId
    GROUP BY
        r.id,
        r.sala,
        r.inicioMillis,
        r.finMillis,
        r.estado,
        r.creadaMillis
    ORDER BY r.creadaMillis DESC, r.id DESC
    """
    )
    fun observarReservasDeUsuario(
        usuarioId: Long
    ): Flow<List<ReservaResumen>>

}
