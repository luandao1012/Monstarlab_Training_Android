package com.example.roomdatabase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.ui.activities.AddNoteActivity
import com.example.roomdatabase.ui.activities.MainActivity
import com.example.roomdatabase.ui.fragments.CalendarFragment
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
        const val ALARM_REMINDER = "ALARM_REMINDER"
        const val CONTENT_REMINDER = "CONTENT_REMINDER"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ALARM_REMINDER) {
            val newIntent = Intent(context?.applicationContext, MainActivity::class.java)
            val bundle = intent.extras
            val noteSerializable = bundle?.getString(CalendarFragment.DATE)
            val note = noteSerializable?.let { Json.decodeFromString<Note>(it) }
            if (bundle != null) {
                newIntent.putExtras(bundle)
            }
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent =
                PendingIntent.getActivity(context?.applicationContext, 0, newIntent, flag)

            val notificationManager =
                context?.applicationContext?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context.applicationContext!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Nhắc nhở")
                    .setContentText(intent.getStringExtra(CONTENT_REMINDER))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            notificationManager.notify(note?.alarmTime?.toInt() ?: 0, builder.build())
        }
    }
}