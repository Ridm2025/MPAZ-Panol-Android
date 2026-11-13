package com.example.MPAZpannol.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categorias",
    indices = [
        Index(value = ["nombre"], unique = true)
    ]
)
data class CategoriaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String
)
