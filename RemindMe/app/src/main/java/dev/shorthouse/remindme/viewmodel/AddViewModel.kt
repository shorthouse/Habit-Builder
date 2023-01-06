package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            val reminderId = repository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                reminder.id = reminderId
                notificationScheduler.scheduleReminderNotification(reminder)
            }
        }
    }
}
