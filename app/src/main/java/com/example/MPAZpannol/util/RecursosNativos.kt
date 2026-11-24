package com.example.MPAZpannol.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object RecursosNativos {

    private const val CANAL_ID = "devoluciones_mpaz"
    private const val NOTIFICACION_ID = 1001

    @Suppress("DEPRECATION")
    fun vibrarConfirmacion(context: Context) {

        try {

            val vibrator = if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            ) {

                context.getSystemService(
                    VibratorManager::class.java
                )?.defaultVibrator

            } else {

                context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {

                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        150L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }

        } catch (_: Exception) {
            // La falta de vibración no afecta al préstamo.
        }
    }

    fun tienePermisoNotificaciones(
        context: Context
    ): Boolean {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        } else {
            true
        }
    }

    fun mostrarRecordatorio(
        context: Context,
        titulo: String,
        mensaje: String,
        notificacionId: Int = NOTIFICACION_ID
    ): Boolean {

        if (!tienePermisoNotificaciones(context)) {
            return false
        }

        return try {

            val manager = context.getSystemService(
                NotificationManager::class.java
            ) ?: return false

            val canal = NotificationChannel(
                CANAL_ID,
                "Devoluciones de equipos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    "Recordatorios de préstamos del pañol"
            }

            manager.createNotificationChannel(canal)

            if (!manager.areNotificationsEnabled()) {
                return false
            }

            if (
                manager.getNotificationChannel(CANAL_ID)
                    ?.importance ==
                NotificationManager.IMPORTANCE_NONE
            ) {
                return false
            }

            val notificacion = Notification.Builder(
                context,
                CANAL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(mensaje)
                )
                .setVisibility(
                    Notification.VISIBILITY_PRIVATE
                )
                .setAutoCancel(true)
                .setTimeoutAfter(30 * 60 * 1000L)
                .build()

            manager.notify(
                notificacionId,
                notificacion
            )

            true

        } catch (_: Exception) {
            false
        }
    }
}
