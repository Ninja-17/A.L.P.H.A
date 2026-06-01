package com.alpha.assistant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlphaForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    // Agregamos una referencia al motor de captura
    val screenEngine = com.alpha.assistant.screen.ScreenCaptureEngine(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // ... (el código que ya tenías)
        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        val restartIntent = Intent(this, AlphaForegroundService::class.java)
        startService(restartIntent)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "A.L.P.H.A.",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "A.L.P.H.A. está vivo y vigilando"
            setShowBadge(false)
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("A.L.P.H.A. está vivo")
            .setContentText("Vigilando en segundo plano")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "alpha_channel"
        const val NOTIFICATION_ID = 1001
    }
}
