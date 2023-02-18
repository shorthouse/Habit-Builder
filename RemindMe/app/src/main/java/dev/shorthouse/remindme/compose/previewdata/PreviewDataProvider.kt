package dev.shorthouse.remindme.compose.previewdata

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.addNotes
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.addNotification
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.addRepeatInterval
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.completed
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.default
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.empty
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.overdue
import dev.shorthouse.remindme.compose.previewdata.ReminderPreviewData.scheduled
import dev.shorthouse.remindme.compose.state.ReminderState
import java.time.LocalTime

class DefaultReminderProvider : PreviewParameterProvider<ReminderState> {
    override val values = sequenceOf(
        default
    )
}

class EmptyReminderProvider : PreviewParameterProvider<ReminderState> {
    override val values = sequenceOf(
        empty
    )
}

class ReminderListProvider : PreviewParameterProvider<List<ReminderState>> {
    override val values = sequenceOf(
        listOf(
            scheduled,
            overdue,
            completed,
            addNotification,
            addRepeatInterval,
            addNotes
        ),
        emptyList()
    )
}

class ReminderListCardProvider : PreviewParameterProvider<ReminderState> {
    override val values = sequenceOf(
        scheduled,
        overdue,
        completed,
        addNotification,
        addRepeatInterval,
        addNotes
    )
}

private object ReminderPreviewData {
    val empty = ReminderState()

    val default = ReminderState(
        id = 1,
        name = "Yoga with Alice",
        date = "Wed, 22 Mar 2000",
        time = LocalTime.of(14, 30),
        isNotificationSent = true,
        isRepeatReminder = true,
        repeatAmount = "2",
        repeatUnit = "Weeks",
        notes = "Don't forget to warm up!",
        isCompleted = false
    )

    val scheduled = ReminderState(
        id = 2,
        name = "Scheduled",
        date = "Sat, 01 Jan 3020",
        time = LocalTime.of(8, 30),
        isNotificationSent = false,
        isRepeatReminder = false,
        repeatAmount = "",
        repeatUnit = "",
        notes = null,
        isCompleted = false
    )

    val overdue = ReminderState(
        id = 3,
        name = "Overdue",
        date = "Wed, 01 Jan 2020",
        time = LocalTime.of(8, 30),
        isNotificationSent = false,
        isRepeatReminder = false,
        repeatAmount = "",
        repeatUnit = "",
        notes = null,
        isCompleted = false
    )

    val completed = ReminderState(
        id = 4,
        name = "Completed",
        date = "Wed, 01 Jan 2020",
        time = LocalTime.of(8, 30),
        isNotificationSent = false,
        isRepeatReminder = false,
        repeatAmount = "",
        repeatUnit = "",
        notes = null,
        isCompleted = true
    )

    val addNotification = ReminderState(
        id = 5,
        name = "Add notification",
        date = "Sat, 01 Jan 3020",
        time = LocalTime.of(8, 30),
        isNotificationSent = true,
        isRepeatReminder = false,
        repeatAmount = "",
        repeatUnit = "",
        notes = null,
        isCompleted = false
    )

    val addRepeatInterval = ReminderState(
        id = 6,
        name = "Add repeat interval",
        date = "Sat, 01 Jan 3020",
        time = LocalTime.of(8, 30),
        isNotificationSent = true,
        isRepeatReminder = true,
        repeatAmount = "4",
        repeatUnit = "days",
        notes = null,
        isCompleted = false
    )

    val addNotes = ReminderState(
        id = 7,
        name = "Add notes",
        date = "Sat, 01 Jan 3020",
        time = LocalTime.of(8, 30),
        isNotificationSent = true,
        isRepeatReminder = true,
        repeatAmount = "4",
        repeatUnit = "days",
        notes = "Don't forget to do this thing",
        isCompleted = false
    )
}
