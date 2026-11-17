package com.example.MPAZpannol.data.local

data class ReservaResumen(
    val id: Long,
    val sala: String,
    val inicioMillis: Long,
    val finMillis: Long,
    val estado: String,
    val equiposResumen: String,
    val motivoRechazo: String? = null,
    val entregaRealMillis: Long? = null,
    val devolucionRealMillis: Long? = null
)
