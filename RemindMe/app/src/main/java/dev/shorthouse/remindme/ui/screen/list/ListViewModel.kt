package dev.shorthouse.remindme.ui.screen.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderOccurrenceUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.util.enums.ReminderAction
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase,
    private val completeRepeatReminderOccurrenceUseCase: CompleteRepeatReminderOccurrenceUseCase,
    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {
    fun processReminderAction(
        selectedReminderState: ReminderState,
        reminderAction: ReminderAction,
        onEdit: () -> Unit
    ) {
        val reminder = selectedReminderState.toReminder()

        when (reminderAction) {
            ReminderAction.EDIT -> onEdit()
            ReminderAction.COMPLETE_ONETIME -> completeOnetimeReminderUseCase(reminder)
            ReminderAction.COMPLETE_REPEAT_OCCURRENCE -> completeRepeatReminderOccurrenceUseCase(
                reminder
            )
            ReminderAction.COMPLETE_REPEAT_SERIES -> completeRepeatReminderSeriesUseCase(reminder)
            ReminderAction.DELETE -> deleteReminderUseCase(reminder)
        }
    }
}
