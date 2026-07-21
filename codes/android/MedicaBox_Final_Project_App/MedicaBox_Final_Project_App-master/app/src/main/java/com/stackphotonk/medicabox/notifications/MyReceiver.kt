package com.stackphotonk.medicabox.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.ui.MainActivity
import java.util.Date

class MyReceiver : BroadcastReceiver() {
    private val channelId = "alarm_notification_channel"
    private val channelName = "Alarmes de Medicamentos"

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val id = intent?.getIntExtra("alarm_id", 0) ?: 0
        val title = intent?.getStringExtra("title") ?: "MedicaBox"
        val message = intent?.getStringExtra("message") ?: "Hora do Remédio!"
        val name = intent?.getStringExtra("name") ?: "Medicamento"
        val hasBox = intent?.getBooleanExtra("hasBox", false)

        Log.d("AlarmDebug", "Alarme acionado: ${Date(System.currentTimeMillis())}")

        // Configurar volume máximo
        setMaxVolume(context)

        // Som do alarme (ringtone ao invés de notificação)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Criar canal de notificação CRÍTICO
        createAlarmNotificationChannel(context, soundUri, audioAttrs)

        var openAppIntent : Intent

        if(hasBox != null) {
            openAppIntent = Intent(context, MainActivity::class.java).apply{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("byNotify", true)
                putExtra("hasBox", hasBox)
                putExtra("name", name)
            }
        } else {
            openAppIntent = Intent(context, MainActivity::class.java).apply{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id, // Pode usar o mesmo id do alarme
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Builder da notificação
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_medic)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri, AudioManager.STREAM_ALARM)
            .setAutoCancel(true)
            .setOngoing(true) // Não deixa swipe dismiss
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000))
            .setContent(getRemoteView(title, message))
            .setContentIntent(pendingIntent)

        // Para Android 12+ (API 31+) - Bypass DND
        builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        val notificationManager = NotificationManagerCompat.from(context)

        // Verificar se pode bypassar DND
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!nm.isNotificationPolicyAccessGranted) {
                Log.w("MyReceiver", "Sem permissão para bypassar Não Perturbe")
            }
        }

        notificationManager.notify(id, builder.build())
    }

    private fun setMaxVolume(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // Salvar volume anterior (opcional, para restaurar depois)
            val previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

            // Setar volume máximo para ALARME
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                maxVolume,
                0 // Sem flag de UI para não mostrar o controle
            )

            Log.d("MyReceiver", "Volume ajustado para máximo: $maxVolume (anterior: $previousVolume)")
        } catch (e: Exception) {
            Log.e("MyReceiver", "Erro ao ajustar volume", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAlarmNotificationChannel(
        context: Context,
        soundUri: android.net.Uri,
        audioAttrs: AudioAttributes
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações críticas de medicamentos"

            // Som personalizado
            setSound(soundUri, audioAttrs)

            // Vibração forte
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)

            // Bypass DND se possível (requer permissão ACCESS_NOTIFICATION_POLICY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setAllowBubbles(true)
            }

            // LED (se disponível no device)
            enableLights(true)
            lightColor = android.graphics.Color.RED

            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.stackphotonk.medicabox", R.layout.notify)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        return remoteView
    }
}