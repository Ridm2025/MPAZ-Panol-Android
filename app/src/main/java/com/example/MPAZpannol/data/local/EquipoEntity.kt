package com.example.MPAZpannol.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "equipos",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["categoriaId"])
    ]
)
data class EquipoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,

    val categoriaId: Long,

    val descripcion: String,

    val stockTotal: Int,

    val stockDisponible: Int,

    val activo: Boolean = true
)
