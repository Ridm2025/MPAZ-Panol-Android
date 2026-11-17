package com.example.MPAZpannol.data.local

data class ReservaPendienteAdmin(
    val id: Long,
    val usuarioId: Long,
    val nombreSolicitante: String,
    val correoSolicitante: String,
    val sala: String,
    val inicioMillis: Long,
    val finMillis: Long,
    val estado: String,
    val equiposResumen: String
)
