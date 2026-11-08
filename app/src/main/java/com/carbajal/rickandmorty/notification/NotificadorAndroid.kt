package com.carbajal.rickandmorty.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.carbajal.rickandmorty.MainActivity
import com.carbajal.rickandmorty.R

class NotificadorAndroid(private val context: Context) : Notificador {
    companion object { private const val CANAL = "sincronizacion" }
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Sincronización del catálogo", NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }
    override fun mostrar(titulo: String, texto: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return
        val intent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val abrir = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val aviso = NotificationCompat.Builder(context, CANAL).setSmallIcon(R.drawable.ic_portal)
            .setContentTitle(titulo).setContentText(texto).setContentIntent(abrir).setAutoCancel(true).build()
        try { manager.notify(1, aviso) } catch (_: SecurityException) { /* El permiso puede revocarse mientras se sincroniza. */ }
    }
}
