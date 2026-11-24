package com.example.MPAZpannol.data.local

data class InventarioResumen(
    val id: Long,
    val nombre: String,
    val categoria: String,
    val stockTotal: Int,
    val stockOperativo: Int,
    val unidadesPrestadas: Long,
    val libresFisicamente: Long,
    val activo: Boolean,
    val ubicacion: String
)
