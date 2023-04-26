package dev.shorthouse.remindme.ui.details

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.component.dialog.ReminderDatePicker
import dev.shorthouse.remindme.ui.component.dialog.ReminderTimePicker
import dev.shorthouse.remindme.ui.component.dialog.RepeatIntervalDialog
import dev.shorthouse.remindme.ui.component.text.RemindMeTextField
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.input.ReminderRepeatIntervalInput
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderStateProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme

@Destination
@Composable
fun ReminderDetailsScreen(
    viewModel: ReminderDetailsViewModel = hiltViewModel(),
    reminderId: Long,
    navigator: DestinationsNavigator
) {
    viewModel.setReminder(reminderId = reminderId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reminderState = ReminderState(uiState.initialReminder)

    if (!uiState.isLoading) {
        ReminderDetailsScaffold(
            reminderState = reminderState,
            onHandleEvent = { viewModel.handleEvent(it) },
            onNavigateUp = { navigator.navigateUp() }
        )
    }
}

@Composable
fun ReminderDetailsScaffold(
    reminderState: ReminderState,
    onHandleEvent: (ReminderDetailsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ReminderDetailsTopBar(
                reminderState = reminderState,
                onHandleEvent = onHandleEvent,
                onNavigateUp = onNavigateUp
            )
        },
        content = { scaffoldPadding ->
            ReminderDetailsContent(
                reminderState = reminderState,
                onHandleEvent = onHandleEvent,
                onNavigateUp = onNavigateUp,
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize()
            )
        },
        modifier = modifier
    )
}

@Composable
fun ReminderDetailsTopBar(
    reminderState: ReminderState,
    onHandleEvent: (ReminderDetailsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    RemindMeTopAppBar(
        title = stringResource(R.string.top_bar_title_reminder_details),
        navigationIcon = {
            IconButton(onClick = { onNavigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder)
                )
            }
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_more)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(
                    x = 0.dp,
                    y = (-60).dp
                )
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.dropdown_delete_reminder),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp)
                        )
                    },
                    onClick = {
                        onHandleEvent(
                            ReminderDetailsEvent.DeleteReminder(reminderState.toReminder())
                        )
                        onNavigateUp()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.dropdown_complete_reminder),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp)
                        )
                    },
                    onClick = {
                        onHandleEvent(
                            ReminderDetailsEvent.CompleteReminder(reminderState.toReminder())
                        )
                        onNavigateUp()
                    }
                )
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailsContent(
    reminderState: ReminderState,
    onHandleEvent: (ReminderDetailsEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        RemindMeTextField(
            text = reminderState.name,
            onTextChange = { if (it.length <= 200) reminderState.name = it },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            hintText = stringResource(R.string.hint_reminder_name),
            imeAction = ImeAction.Done
        )

        var isDatePickerShown by remember { mutableStateOf(false) }
        if (isDatePickerShown) {
            ReminderDatePicker(
                initialDate = reminderState.date,
                onConfirm = { reminderState.date = it },
                onDismiss = { isDatePickerShown = false }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDatePickerShown = true }
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarToday,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = stringResource(R.string.cd_details_date)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = reminderState.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        var isTimePickerShown by remember { mutableStateOf(false) }
        if (isTimePickerShown) {
            ReminderTimePicker(
                initialTime = reminderState.time,
                onConfirm = { reminderState.time = it },
                onDismiss = { isTimePickerShown = false }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isTimePickerShown = true }
        ) {
            Icon(
                imageVector = Icons.Rounded.Schedule,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = stringResource(R.string.cd_details_time)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = reminderState.time.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row {
            FilterChip(
                selected = reminderState.isNotificationSent,
                onClick = { reminderState.isNotificationSent = !reminderState.isNotificationSent },
                label = {
                    Text(
                        text = if (reminderState.isNotificationSent) {
                            "Notification on"
                        } else {
                            "Add notification"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsNone,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            FilterChip(
                selected = reminderState.isRepeatReminder,
                onClick = { reminderState.isRepeatReminder = !reminderState.isRepeatReminder },
                label = {
                    Text(
                        text = if (reminderState.isRepeatReminder) {
                            "5 days"
                        } else {
                            "Add repeat"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )
         
            if (reminderState.isRepeatReminder) {
                RepeatIntervalDialog(
                    reminderState = reminderState,
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }

        ReminderRepeatIntervalInput(
            reminderState = reminderState
        )

        Row {
            Icon(
                imageVector = Icons.Rounded.Notes,
                contentDescription = stringResource(R.string.cd_details_notes),
                tint = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.width(16.dp))

            RemindMeTextField(
                text = reminderState.notes.orEmpty(),
                onTextChange = { if (it.length <= 2000) reminderState.notes = it },
                textStyle = MaterialTheme.typography.bodyMedium
                    .copy(color = MaterialTheme.colorScheme.onBackground),
                hintText = stringResource(R.string.hint_reminder_notes),
                imeAction = ImeAction.None,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onHandleEvent(ReminderDetailsEvent.SaveReminder(reminderState.toReminder()))
                onNavigateUp()
            },
            content = {
                Text(
                    text = stringResource(R.string.button_save_reminder),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            enabled = reminderState.isValid(),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReminderDateInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    var isDatePickerShown by remember { mutableStateOf(false) }

    if (isDatePickerShown) {
        ReminderDatePicker(
            initialDate = reminderState.date,
            onConfirm = { reminderState.date = it },
            onDismiss = { isDatePickerShown = false }
        )
    }

    TextWithLeftIcon(
        icon = Icons.Rounded.CalendarToday,
        text = reminderState.date,
        modifier = modifier.clickable { isDatePickerShown = true },
        contentDescription = stringResource(R.string.cd_details_date)
    )
}

@Composable
fun ReminderTimeInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    var isTimePickerShown by remember { mutableStateOf(false) }

    if (isTimePickerShown) {
        ReminderTimePicker(
            initialTime = reminderState.time,
            onConfirm = { reminderState.time = it },
            onDismiss = { isTimePickerShown = false }
        )
    }

    TextWithLeftIcon(
        icon = Icons.Rounded.Schedule,
        text = reminderState.time.toString(),
        modifier = modifier.clickable { isTimePickerShown = true },
        contentDescription = stringResource(R.string.cd_details_time)
    )
}

@Composable
fun ReminderSwitchRow(
    icon: ImageVector,
    iconContentDescription: String?,
    switchText: String,
    switchTestTag: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        TextWithLeftIcon(
            icon = icon,
            text = switchText,
            contentDescription = iconContentDescription
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(switchTestTag)
        )
    }
}

@Composable
fun ReminderNotificationInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    ReminderSwitchRow(
        icon = Icons.Rounded.NotificationsNone,
        iconContentDescription = stringResource(R.string.cd_details_notification),
        switchText = stringResource(R.string.title_send_notification),
        switchTestTag = stringResource(R.string.test_tag_switch_notification),
        isChecked = reminderState.isNotificationSent,
        onCheckedChange = { reminderState.isNotificationSent = it },
        modifier = modifier
    )
}

@Composable
fun ReminderNotesInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.Notes,
            contentDescription = stringResource(R.string.cd_details_notes),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.width(16.dp))

        RemindMeTextField(
            text = reminderState.notes.orEmpty(),
            onTextChange = { if (it.length <= 2000) reminderState.notes = it },
            textStyle = MaterialTheme.typography.bodyMedium
                .copy(color = MaterialTheme.colorScheme.onSurface),
            hintText = stringResource(R.string.hint_reminder_notes),
            imeAction = ImeAction.None,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReminderRepeatIntervalInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    ReminderSwitchRow(
        icon = Icons.Rounded.Refresh,
        iconContentDescription = null,
        switchText = stringResource(R.string.title_repeat_reminder),
        switchTestTag = stringResource(R.string.test_tag_switch_repeat_interval),
        isChecked = reminderState.isRepeatReminder,
        onCheckedChange = { reminderState.isRepeatReminder = it },
        modifier = modifier
    )

    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    reminderState.repeatUnit = when {
        stringResource(R.string.day) in reminderState.repeatUnit -> pluralStringResource(
            R.plurals.repeat_unit_days,
            repeatAmount
        )
        else -> pluralStringResource(
            R.plurals.repeat_unit_weeks,
            repeatAmount
        )
    }

    if (reminderState.isRepeatReminder) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.repeats_every_header),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    RepeatAmountInput(reminderState = reminderState)
                }

                Spacer(Modifier.width(24.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RepeatUnitInput(reminderState = reminderState)
                }
            }
        }
    }
}

@Composable
private fun RepeatAmountInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = reminderState.repeatAmount,
        onValueChange = { repeatAmount ->
            if (repeatAmount.length <= 2) {
                reminderState.repeatAmount = repeatAmount
                    .trimStart { it == '0' }
                    .filter { it.isDigit() }
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier
            .width(72.dp)
            .padding(end = 16.dp)
            .testTag(stringResource(R.string.test_tag_text_field_repeat_amount))
    )
}

@Composable
private fun RepeatUnitInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    val repeatUnitPluralIds = listOf(R.plurals.repeat_unit_days, R.plurals.repeat_unit_weeks)
    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    val repeatUnitOptions = repeatUnitPluralIds.map { pluralId ->
        pluralStringResource(
            pluralId,
            repeatAmount
        )
    }

    Column(modifier = modifier) {
        repeatUnitOptions.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (text == reminderState.repeatUnit),
                        onClick = { reminderState.repeatUnit = text }
                    )
                    .fillMaxWidth(0.8f)
            ) {
                RadioButton(
                    selected = (text == reminderState.repeatUnit),
                    onClick = { reminderState.repeatUnit = text },
                    modifier = Modifier.testTag(text)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TextWithLeftIcon(
    icon: ImageVector,
    text: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderInputPreview(
    @PreviewParameter(DefaultReminderStateProvider::class) reminderState: ReminderState
) {
    AppTheme {
        ReminderDetailsScaffold(
            reminderState = reminderState,
            onHandleEvent = {},
            onNavigateUp = {}
        )
    }
}
