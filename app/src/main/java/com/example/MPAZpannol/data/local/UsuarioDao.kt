package com.example.MPAZpannol.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query(
        "SELECT * FROM usuarios WHERE correo = :correo LIMIT 1"
    )
    suspend fun buscarPorCorreo(
        correo: String
    ): UsuarioEntity?
}
