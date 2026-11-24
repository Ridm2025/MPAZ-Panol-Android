package com.example.MPAZpannol.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MPAZpannol.data.local.MPAZDatabase
import com.example.MPAZpannol.util.RecursosNativos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

class RecordatorioWorker(
    context: Context,
    parametros: WorkerParameters
) : CoroutineWorker(context, parametros) {

    override suspend fun doWork(): Result {

        return try {

            val context = applicationContext

            val administradorId =
                ProgramadorRecordatorios
                    .administradorActivo(context)

            if (administradorId <= 0L) {
                return Result.success()
            }

            val database =
                MPAZDatabase.getInstance(context)

            val dao = database.reservaDao()

            val rol = dao.obtenerRolUsuario(
                administradorId
            )

            if (rol != "ADMINISTRADOR") {
                return Result.success()
            }

            val prestamos = dao
                .observarPrestamosEnGestion()
                .first()

            val ahora = System.currentTimeMillis()

            val proximas = prestamos.filter {

                it.estado == "ENTREGADA" &&
                        it.finMillis > ahora &&
                        it.finMillis - ahora <= 1800000L
            }

            val atrasadas = prestamos.filter {

                it.estado == "ENTREGADA" &&
                        it.finMillis <= ahora
            }

            val alertasActuales = (
                    proximas.map {
                        "${it.id}:PROXIMA"
                    } +
                            atrasadas.map {
                                "${it.id}:ATRASADA"
                            }
                    ).toSet()

            if (
                ProgramadorRecordatorios
                    .administradorActivo(context)
                != administradorId
            ) {
                return Result.success()
            }

            val preferencias =
                ProgramadorRecordatorios
                    .preferencias(context)

            val alertasAnteriores =
                preferencias.getStringSet(
                    "alertasEnviadas",
                    emptySet()
                )?.toSet() ?: emptySet()

            if (alertasActuales.isEmpty()) {

                preferencias.edit()
                    .remove("alertasEnviadas")
                    .apply()

                ProgramadorRecordatorios
                    .cancelarNotificacion(context)

                return Result.success()
            }

            val nuevasAlertas =
                alertasActuales - alertasAnteriores

            if (nuevasAlertas.isEmpty()) {

                preferencias.edit()
                    .putStringSet(
                        "alertasEnviadas",
                        alertasActuales
                    )
                    .apply()

                return Result.success()
            }

            val enviada =
                RecursosNativos.mostrarRecordatorio(
                    context = context,
                    titulo = "Recordatorio de devoluciones",
                    mensaje =
                        "Hay préstamos que requieren revisión. " +
                                "Ingresa a MPAZ Pañol para consultar su estado.",
                    notificacionId =
                        ProgramadorRecordatorios
                            .AVISO_AUTOMATICO_ID
                )

            if (enviada) {

                preferencias.edit()
                    .putStringSet(
                        "alertasEnviadas",
                        alertasActuales
                    )
                    .apply()
            }

            Result.success()

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            Result.retry()
        }
    }
}
