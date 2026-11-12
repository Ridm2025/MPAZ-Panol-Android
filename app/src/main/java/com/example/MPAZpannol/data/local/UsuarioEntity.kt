package com.example.MPAZpannol.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,
    val correo: String,

    val passwordHash: String,
    val passwordSalt: String,

    val rol: String = "SOLICITANTE"
)
