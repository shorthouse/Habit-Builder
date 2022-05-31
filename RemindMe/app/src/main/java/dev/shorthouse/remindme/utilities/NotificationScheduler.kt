package dev.shorthouse.remindme.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receivers.AlarmNotificationReceiver
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) {
    fun scheduleReminderNotification(reminder: Reminder) {
        if (reminder.isRepeatReminder()) {
            scheduleRepeatNotification(reminder)
        } else {
            scheduleOneTimeNotification(reminder)
        }
    }

    private fun scheduleOneTimeNotification(reminder: Reminder) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getAlarmTriggerTime(reminder),
            getNotificationBroadcastIntent(reminder)
        )
    }

    private fun scheduleRepeatNotification(reminder: Reminder) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            getAlarmTriggerTime(reminder),
            getAlarmRepeatInterval(reminder),
            getNotificationBroadcastIntent(reminder)
        )
    }

    private fun getNotificationBroadcastIntent(reminder: Reminder): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
        )
    }

    fun cancelExistingReminderNotification(reminder: Reminder) {
        val alarmBroadcastIntent = getExistingBroadcastIntent(reminder)

        alarmBroadcastIntent?.let {
            alarmManager.cancel(alarmBroadcastIntent)
        }
    }

    private fun getExistingBroadcastIntent(reminder: Reminder): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
    }

    private fun getAlarmIntent(reminder: Reminder): Intent {
        return Intent(context, AlarmNotificationReceiver::class.java)
            .putExtra(
                context.getString(R.string.intent_key_reminderId),
                reminder.id
            )
            .putExtra(
                context.getString(R.string.intent_key_notificationTitle),
                getReminderNotificationTitle(reminder)
            )
            .putExtra(
                context.getString(R.string.intent_key_notificationText),
                getReminderNotificationText(context, reminder)
            )
    }

    private fun getAlarmTriggerTime(reminder: Reminder): Long {
        return reminder.startDateTime.toInstant().toEpochMilli()
    }

    private fun getAlarmRepeatInterval(reminder: Reminder): Long {
        val repeatInterval = reminder.repeatInterval!!

        val repeatIntervalDays = when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> repeatInterval.timeValue
            else -> repeatInterval.timeValue * DAYS_IN_WEEK
        }

        return Duration.ofDays(repeatIntervalDays).toMillis()
    }

    private fun getReminderNotificationTitle(reminder: Reminder): String {
        return reminder.name
    }

    private fun getReminderNotificationText(context: Context, reminder: Reminder): String {
        val formattedStartTime = reminder.startDateTime.toLocalTime().toString()

        return context.getString(
            R.string.reminder_notification_body,
            formattedStartTime
        )
    }
}
