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
        r.motivoRechazo AS motivoRechazo,
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
        r.motivoRechazo,
        r.creadaMillis
    ORDER BY r.creadaMillis DESC, r.id DESC
    """
    )
    fun observarReservasDeUsuario(
        usuarioId: Long
    ): Flow<List<ReservaResumen>>

    @Query(
        """
    SELECT
        r.id AS id,
        r.usuarioId AS usuarioId,
        u.nombre AS nombreSolicitante,
        u.correo AS correoSolicitante,
        r.sala AS sala,
        r.inicioMillis AS inicioMillis,
        r.finMillis AS finMillis,
        r.estado AS estado,
        r.motivoRechazo AS motivoRechazo,
        COALESCE(
            GROUP_CONCAT(
                e.nombre || ' x' || d.cantidad,
                ', '
            ),
            'Sin equipos'
        ) AS equiposResumen
    FROM reservas AS r
    INNER JOIN usuarios AS u
        ON r.usuarioId = u.id
    LEFT JOIN reserva_detalles AS d
        ON r.id = d.reservaId
    LEFT JOIN equipos AS e
        ON d.equipoId = e.id
    WHERE r.estado = 'PENDIENTE'
    GROUP BY
        r.id,
        r.usuarioId,
        u.nombre,
        u.correo,
        r.sala,
        r.inicioMillis,
        r.finMillis,
        r.estado,
        r.creadaMillis
    ORDER BY r.creadaMillis ASC, r.id ASC
    """
    )
    fun observarReservasPendientes():
            Flow<List<ReservaPendienteAdmin>>


    @Query(
        "SELECT rol FROM usuarios WHERE id = :usuarioId LIMIT 1"
    )
    suspend fun obtenerRolUsuario(
        usuarioId: Long
    ): String?

    @Query(
        "SELECT * FROM reservas WHERE id = :reservaId LIMIT 1"
    )
    suspend fun obtenerReservaPorId(
        reservaId: Long
    ): ReservaEntity?

    @Query(
        "SELECT * FROM reserva_detalles WHERE reservaId = :reservaId"
    )
    suspend fun obtenerDetalles(
        reservaId: Long
    ): List<ReservaDetalleEntity>

    @Query(
        """
    SELECT COALESCE(SUM(d.cantidad), 0)
    FROM reserva_detalles AS d
    INNER JOIN reservas AS r
        ON d.reservaId = r.id
    WHERE d.equipoId = :equipoId
      AND r.estado IN ('ACEPTADA', 'ENTREGADA')
      AND r.inicioMillis < :finMillis
      AND r.finMillis > :inicioMillis
    """
    )
    suspend fun obtenerCantidadComprometida(
        equipoId: Long,
        inicioMillis: Long,
        finMillis: Long
    ): Int

    @Query(
        """
    UPDATE reservas
    SET estado = :nuevoEstado,
        motivoRechazo = :motivoRechazo
    WHERE id = :reservaId
      AND estado = 'PENDIENTE'
    """
    )
    suspend fun actualizarEstadoPendiente(
        reservaId: Long,
        nuevoEstado: String,
        motivoRechazo: String?
    ): Int

    @Query(
        """
    SELECT
        r.id AS id,
        u.nombre AS nombreSolicitante,
        u.correo AS correoSolicitante,
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
    INNER JOIN usuarios AS u
        ON r.usuarioId = u.id
    LEFT JOIN reserva_detalles AS d
        ON r.id = d.reservaId
    LEFT JOIN equipos AS e
        ON d.equipoId = e.id
    WHERE r.estado IN (
        'ACEPTADA',
        'ENTREGADA',
        'DEVUELTA'
    )
    GROUP BY
        r.id,
        u.nombre,
        u.correo,
        r.sala,
        r.inicioMillis,
        r.finMillis,
        r.estado
    ORDER BY r.inicioMillis DESC, r.id DESC
    """
    )
    fun observarPrestamosEnGestion():
            Flow<List<ReservaGestionAdmin>>

    @Query(
        """
    UPDATE reservas
    SET estado = :nuevoEstado
    WHERE id = :reservaId
      AND estado = :estadoAnterior
    """
    )
    suspend fun actualizarEstadoPrestamo(
        reservaId: Long,
        estadoAnterior: String,
        nuevoEstado: String
    ): Int
}
