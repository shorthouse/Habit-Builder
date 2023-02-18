package dev.shorthouse.remindme.compose.component.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.emptystate.EmptyStateActiveReminders
import dev.shorthouse.remindme.compose.previewdata.ReminderListProvider
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme

@Composable
fun ReminderListContent(
    reminderStates: List<ReminderState>,
    emptyStateContent: @Composable () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    if (reminderStates.isEmpty()) {
        emptyStateContent()
    } else {
        ReminderList(
            reminderStates = reminderStates,
            onReminderCard = onReminderCard,
            contentPadding = contentPadding,
            modifier = modifier
        )
    }
}

@Composable
fun ReminderList(
    reminderStates: List<ReminderState>,
    onReminderCard: (ReminderState) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_tiny)),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        items(reminderStates) { reminderState ->
            ReminderListCard(
                reminderState = reminderState,
                onReminderCard = onReminderCard
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun ReminderListContentPreview(
    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
) {
    RemindMeTheme {
        ReminderListContent(
            reminderStates = reminderStates,
            emptyStateContent = { EmptyStateActiveReminders() },
            contentPadding = PaddingValues(dimensionResource(R.dimen.margin_tiny)),
            onReminderCard = {},
        )
    }
}
