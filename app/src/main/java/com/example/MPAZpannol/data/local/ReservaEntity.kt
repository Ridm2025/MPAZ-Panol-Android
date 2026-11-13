package com.example.MPAZpannol.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["usuarioId"])
    ]
)
data class ReservaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val usuarioId: Long,

    val sala: String,

    val inicioMillis: Long,

    val finMillis: Long,

    val estado: String = "PENDIENTE",

    val creadaMillis: Long
)
