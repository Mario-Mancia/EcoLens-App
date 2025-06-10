package com.example.ecolens.hardware.stepsensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ecolens.R

class StepForegroundService : Service() {

    private lateinit var stepSensorManager: StepSensorManager
    private var stepCount: Int = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        stepSensorManager = StepSensorManager(applicationContext) {
            stepCount++
            sendStepUpdateBroadcast()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        stepSensorManager.startListening()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stepSensorManager.stopListening()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "step_channel_id")
            .setContentTitle("EcoLens Podómetro activo")
            .setContentText("Contando tus pasos...")
            .setSmallIcon(R.drawable.footsteps_icon) // Asegúrate de tener un icono apropiado
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "step_channel_id",
                "EcoLens Podómetro",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun sendStepUpdateBroadcast() {
        val intent = Intent("com.example.ecolens.STEP_UPDATE")
        intent.putExtra("stepCount", stepCount)
        sendBroadcast(intent)
    }
}