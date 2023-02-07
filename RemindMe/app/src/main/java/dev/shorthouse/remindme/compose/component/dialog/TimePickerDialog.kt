package dev.shorthouse.remindme.compose.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.previewdata.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.Black
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.LightGrey
import dev.shorthouse.remindme.theme.White

@Composable
fun TimePickerDialog(
    reminderState: ReminderState,
    dialogState: MaterialDialogState
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_action_ok),
                textStyle = MaterialTheme.typography.button
            )
            negativeButton(
                text = stringResource(R.string.dialog_action_cancel),
                textStyle = MaterialTheme.typography.button
            )
        },
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_normal)))

        timepicker(
            initialTime = reminderState.time,
            title = "",
            is24HourClock = true,
            onTimeChange = { reminderState.time = it },
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = MaterialTheme.colors.primary,
                activeTextColor = White,
                inactiveBackgroundColor = LightGrey,
                inactiveTextColor = Black,
                selectorColor = MaterialTheme.colors.primary,
                selectorTextColor = White,
            )
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TimePickerDialogPreview() {
    RemindMeTheme {
        TimePickerDialog(
            reminderState = PreviewData.previewReminderState,
            dialogState = MaterialDialogState(initialValue = true)
        )
    }
}
