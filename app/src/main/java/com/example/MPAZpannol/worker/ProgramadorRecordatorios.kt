package com.example.MPAZpannol.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ProgramadorRecordatorios {

    private const val TRABAJO =
        "recordatorios_devolucion_mpaz"

    const val AVISO_AUTOMATICO_ID = 1002

    fun preferencias(context: Context) =
        context.getSharedPreferences(
            "recordatorios_mpaz",
            Context.MODE_PRIVATE
        )

    fun administradorActivo(context: Context): Long {

        return preferencias(context)
            .getLong("administradorId", 0L)
    }

    fun activar(
        context: Context,
        administradorId: Long
    ) {

        require(administradorId > 0L)

        if (
            administradorActivo(context) != administradorId
        ) {

            preferencias(context).edit()
                .clear()
                .putLong(
                    "administradorId",
                    administradorId
                )
                .apply()
        }

        val trabajo =
            PeriodicWorkRequestBuilder<RecordatorioWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                TRABAJO,
                ExistingPeriodicWorkPolicy.KEEP,
                trabajo
            )
    }

    fun desactivar(context: Context) {

        preferencias(context)
            .edit()
            .clear()
            .apply()

        WorkManager.getInstance(context)
            .cancelUniqueWork(TRABAJO)

        cancelarNotificacion(context)
    }

    fun cancelarNotificacion(context: Context) {

        context.getSystemService(
            NotificationManager::class.java
        )?.cancel(AVISO_AUTOMATICO_ID)
    }
}
