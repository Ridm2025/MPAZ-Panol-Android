package com.example.MPAZpannol.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reserva_detalles",
    foreignKeys = [
        ForeignKey(
            entity = ReservaEntity::class,
            parentColumns = ["id"],
            childColumns = ["reservaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EquipoEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["reservaId"]),
        Index(value = ["equipoId"])
    ]
)
data class ReservaDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val reservaId: Long,

    val equipoId: Long,

    val cantidad: Int
)
