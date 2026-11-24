package com.example.MPAZpannol.util

import com.example.MPAZpannol.data.local.ReservaGestionAdmin

data class ResumenDevoluciones(
    val proximas: Int,
    val atrasadas: Int
)

object RecordatoriosDevolucion {

    private const val TREINTA_MINUTOS =
        30 * 60 * 1000L

    fun obtenerResumen(
        prestamos: List<ReservaGestionAdmin>,
        ahora: Long = System.currentTimeMillis()
    ): ResumenDevoluciones {

        val entregados = prestamos.filter {
            it.estado == "ENTREGADA"
        }

        val atrasadas = entregados.count {
            it.finMillis <= ahora
        }

        val proximas = entregados.count {

            val tiempoRestante =
                it.finMillis - ahora

            tiempoRestante > 0 &&
                    tiempoRestante <= TREINTA_MINUTOS
        }

        return ResumenDevoluciones(
            proximas = proximas,
            atrasadas = atrasadas
        )
    }
}
