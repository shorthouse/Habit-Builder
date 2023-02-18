package dev.shorthouse.remindme.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.MainActivity
import dev.shorthouse.remindme.R
import javax.inject.Inject

@AndroidEntryPoint
class ReminderNotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        createNotificationChannel(context)
        displayReminderNotification(context, intent)
    }

    private fun createNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            context.getString(R.string.notification_channel_id_reminder),
            context.getString(R.string.notification_channel_name_reminder),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun displayReminderNotification(context: Context, intent: Intent) {
        val reminderNotificationId = intent.getLongExtra(
            context.getString(R.string.intent_key_reminderId),
            -1L
        )
            .toInt()

        val reminderNotification = getReminderNotification(context, intent)

        reminderNotification?.let {
            notificationManager.notify(reminderNotificationId, reminderNotification)
        }
    }

    private fun getReminderNotification(context: Context, intent: Intent): Notification? {
        val reminderId = intent.getLongExtra(context.getString(R.string.intent_key_reminderId), -1L)
        val notificationTitle = intent.getStringExtra(context.getString(R.string.intent_key_notificationTitle))
        val notificationText = intent.getStringExtra(context.getString(R.string.intent_key_notificationText))

        if (!areIntentValuesValid(reminderId, notificationTitle, notificationText)) return null

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id_reminder)
        )
            .setSmallIcon(R.drawable.ic_user_notification)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_user_notification_action,
                context.getString(R.string.reminder_notification_action_complete_text),
                getDonePendingIntent(context, reminderId)
            )
            .setAutoCancel(true)
            .build()
    }

    private fun getDonePendingIntent(context: Context, reminderId: Long): PendingIntent {
        val doneIntent = Intent(context, NotificationActionDoneReceiver::class.java)
            .putExtra(context.getString(R.string.intent_key_reminderId), reminderId)

        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            doneIntent, FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun areIntentValuesValid(
        reminderId: Long,
        notificationTitle: String?,
        notificationText: String?
    ): Boolean {
        return reminderId != -1L && notificationTitle != null && notificationText != null
    }
}
