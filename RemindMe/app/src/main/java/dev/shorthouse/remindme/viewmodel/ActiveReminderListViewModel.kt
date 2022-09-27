package dev.shorthouse.remindme.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.RemindersSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ActiveReminderListViewModel @Inject constructor(
    val repository: ReminderRepository,
) : ViewModel() {
    private val currentAllReminders = repository
        .getNonArchivedReminders()
        .asLiveData()

    @VisibleForTesting
    val currentTime = MutableLiveData(ZonedDateTime.now())

    fun getReminders(
        currentSort: MutableLiveData<RemindersSort>,
        currentFilter: MutableLiveData<String>
    ): LiveData<List<Reminder>> {
        val remindersListData = MediatorLiveData<List<Reminder>>()

        remindersListData.addSource(currentAllReminders) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }
        remindersListData.addSource(currentSort) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }
        remindersListData.addSource(currentFilter) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }
        remindersListData.addSource(currentTime) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }

        return remindersListData
    }

    private fun getRemindersListData(
        currentSort: MutableLiveData<RemindersSort>,
        currentFilter: MutableLiveData<String>
    ): List<Reminder>? {
        val allReminders = currentAllReminders.value
        val time = currentTime.value
        val sort = currentSort.value
        val filter = currentFilter.value

        if (allReminders == null || time == null || sort == null || filter == null) return null

        val activeReminders = allReminders.filter { reminder ->
            reminder.startDateTime.isBefore(time)
        }

        val activeSortedReminders = when (sort) {
            RemindersSort.EARLIEST_DATE_FIRST -> activeReminders.sortedBy { it.startDateTime }
            else -> activeReminders.sortedByDescending { it.startDateTime }
        }

        return if (filter.isBlank()) {
            activeSortedReminders
        } else {
            activeSortedReminders.filter { reminder ->
                reminder.name.contains(filter, true)
            }
        }
    }

    fun updateCurrentTime() {
        currentTime.value = ZonedDateTime.now()
    }

    fun getMillisUntilNextMinute(now: LocalDateTime = LocalDateTime.now()): Long {
        return Duration.between(
            now,
            now.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)
        )
            .toMillis()
    }

    fun undoDoneReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder)
        }
    }

    fun updateDoneReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(getUpdatedDoneReminder(reminder))
        }
    }

    @VisibleForTesting
    fun getUpdatedDoneReminder(reminder: Reminder): Reminder {
        return Reminder(
            id = reminder.id,
            name = reminder.name,
            startDateTime = getUpdatedStartDateTime(reminder),
            repeatInterval = reminder.repeatInterval,
            notes = reminder.notes,
            isArchived = !reminder.isRepeatReminder(),
            isNotificationSent = reminder.isNotificationSent,
        )
    }

    private fun getUpdatedStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> Duration.ofDays(repeatInterval.timeValue)
            else -> Duration.ofDays(repeatInterval.timeValue * DAYS_IN_WEEK)
        }

        val passedDuration = Duration.between(reminder.startDateTime, ZonedDateTime.now())

        return reminder.startDateTime
            .plusSeconds(passedDuration
                .dividedBy(repeatDuration)
                .plus(1)
                .times(repeatDuration.toSeconds())
            )
    }
}