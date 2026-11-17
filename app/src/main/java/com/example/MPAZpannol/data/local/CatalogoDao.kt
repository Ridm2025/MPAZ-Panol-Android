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

        e.activo AS activo

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
}
