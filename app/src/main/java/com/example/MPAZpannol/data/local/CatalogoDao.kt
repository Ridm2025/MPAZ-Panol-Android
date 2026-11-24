package com.example.MPAZpannol.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogoDao {

    @Query(
        "SELECT * FROM categorias ORDER BY nombre"
    )
    suspend fun obtenerCategorias(): List<CategoriaEntity>

    @Query(
        """
    SELECT
        e.id AS id,
        e.nombre AS nombre,
        e.categoriaId AS categoriaId,
        e.descripcion AS descripcion,
        e.stockTotal AS stockTotal,

        MAX(
            0,
            e.stockDisponible - COALESCE(
                (
                    SELECT SUM(d.cantidad)
                    FROM reserva_detalles AS d
                    INNER JOIN reservas AS r
                        ON r.id = d.reservaId
                    WHERE d.equipoId = e.id
                      AND r.estado = 'ENTREGADA'
                ),
                0
            )
        ) AS stockDisponible,

        e.activo AS activo,
        e.ubicacion AS ubicacion

    FROM equipos AS e

    WHERE e.activo = 1

    ORDER BY e.nombre
    """
    )
    fun observarEquipos(): Flow<List<EquipoEntity>>

    @Insert
    suspend fun insertarCategoria(
        categoria: CategoriaEntity
    ): Long

    @Insert
    suspend fun insertarEquipo(
        equipo: EquipoEntity
    ): Long

    @Query(
        """
    WITH prestados AS (
        SELECT
            d.equipoId AS equipoId,
            SUM(d.cantidad) AS cantidad
        FROM reserva_detalles AS d
        INNER JOIN reservas AS r
            ON r.id = d.reservaId
        WHERE r.estado = 'ENTREGADA'
        GROUP BY d.equipoId
    )

    SELECT
        e.id AS id,
        e.nombre AS nombre,
        c.nombre AS categoria,
        e.stockTotal AS stockTotal,
        e.stockDisponible AS stockOperativo,

        COALESCE(
            p.cantidad,
            0
        ) AS unidadesPrestadas,

        MAX(
            0,
            e.stockDisponible -
            COALESCE(p.cantidad, 0)
        ) AS libresFisicamente,

        e.activo AS activo,
        e.ubicacion AS ubicacion

    FROM equipos AS e

    INNER JOIN categorias AS c
        ON c.id = e.categoriaId

    LEFT JOIN prestados AS p
        ON p.equipoId = e.id

    ORDER BY e.nombre
    """
    )
    fun observarResumenInventario():
            Flow<List<InventarioResumen>>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun buscarEquipoAdmin(id: Long): EquipoEntity?

    @Query("SELECT * FROM categorias ORDER BY nombre")
    fun observarCategoriasAdmin(): Flow<List<CategoriaEntity>>

    @Insert
    suspend fun crearEquipoAdmin(equipo: EquipoEntity): Long


    @Query(
        """
    UPDATE equipos
    SET nombre = :nombre,
        descripcion = :descripcion,
        categoriaId = :categoriaId,
        stockTotal = :stockTotal,
        stockDisponible = :stockOperativo,
        ubicacion = :ubicacion
    WHERE id = :id
    """
    )
    suspend fun editarEquipoAdmin(
        id: Long,
        nombre: String,
        descripcion: String,
        categoriaId: Long,
        stockTotal: Int,
        stockOperativo: Int,
        ubicacion: String
    ): Int

    suspend fun editarEquipoAdmin(
        id: Long,
        nombre: String,
        descripcion: String,
        categoriaId: Long,
        stockTotal: Int,
        stockOperativo: Int
    ): Int

    @Query("UPDATE equipos SET activo = :activo WHERE id = :id")
    suspend fun cambiarEstadoEquipo(
        id: Long,
        activo: Boolean
    ): Int

    @Query(
        """
    SELECT COUNT(*)
    FROM reserva_detalles AS d
    INNER JOIN reservas AS r
        ON r.id = d.reservaId
    WHERE d.equipoId = :equipoId
      AND r.estado IN (
          'PENDIENTE',
          'ACEPTADA',
          'ENTREGADA'
      )
    """
    )
    suspend fun contarCompromisosActivos(
        equipoId: Long
    ): Int

}
