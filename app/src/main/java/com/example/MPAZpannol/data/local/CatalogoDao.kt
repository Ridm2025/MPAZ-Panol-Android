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
        "SELECT * FROM equipos WHERE activo = 1 ORDER BY nombre"
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
