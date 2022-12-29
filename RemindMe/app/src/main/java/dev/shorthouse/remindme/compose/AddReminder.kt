package dev.shorthouse.remindme.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.AddViewModel

@Composable
fun AddReminderScreen(
    addViewModel: AddViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    //TODO
    // Focus keyboard on reminder name if its add

    val reminderState by remember { mutableStateOf(ReminderState()) }

    val onSave = {
        if (addViewModel.isReminderValid(reminderState.toReminder())) {
            addViewModel.addReminder(reminderState.toReminder())
            onNavigateUp()
        }
    }

    AddReminderScaffold(
        reminderState = reminderState,
        onNavigateUp = onNavigateUp,
        onSave = onSave,
    )
}

@Composable
fun AddReminderScaffold(
    reminderState: ReminderState,
    onSave: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            AddReminderTopBar(
                onNavigateUp = onNavigateUp,
                onSave = onSave
            )
        },
        content = { innerPadding ->
            AddReminderContent(
                innerPadding = innerPadding,
                reminderState = reminderState,
            )
        }
    )
}

@Composable
fun AddReminderTopBar(
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.testTag("AddReminderTopBar"),
        title = {
            Text(
                text = stringResource(R.string.top_bar_title_add_reminder)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder)
                )
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Icon(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = stringResource(R.string.cd_top_bar_save_reminder),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun AddReminderContent(
    innerPadding: PaddingValues,
    reminderState: ReminderState,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_normal),
                end = dimensionResource(R.dimen.margin_normal),
                top = innerPadding.calculateTopPadding(),
            )
    ) {
        val maxNameLength = 200
        RemindMeTextField(
            text = reminderState.name,
            onTextChange = { if (it.length <= maxNameLength) reminderState.name = it },
            textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            hintText = stringResource(R.string.hint_reminder_name),
            imeAction = ImeAction.Done,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.margin_normal))
        )

        TextWithLeftIcon(
            icon = painterResource(R.drawable.ic_calendar),
            text = reminderState.date,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.margin_large))
                .fillMaxWidth()

        )

        TextWithLeftIcon(
            icon = painterResource(R.drawable.ic_clock),
            text = reminderState.time,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.margin_large))
                .fillMaxWidth()
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.margin_small)))

        ReminderSwitchRow(
            icon = painterResource(R.drawable.ic_notification_outline),
            switchText = stringResource(R.string.title_send_notification),
            isChecked = reminderState.isNotificationSent,
            onCheckedChange = { reminderState.isNotificationSent = it }
        )

        ReminderSwitchRow(
            icon = painterResource(R.drawable.ic_repeat),
            switchText = stringResource(R.string.title_repeat_reminder),
            isChecked = reminderState.isRepeatReminder,
            onCheckedChange = { reminderState.isRepeatReminder = it }
        )

        if (reminderState.isRepeatReminder) {
            val maxRepeatAmountLength = 2
            RepeatIntervalInput(
                reminderState = reminderState,
                onRepeatUnitChange = { reminderState.repeatUnit = it },
                onRepeatAmountChange = {
                    if (it.length <= maxRepeatAmountLength) reminderState.repeatAmount = sanitiseRepeatAmount(it)
                }
            )
        }

        val maxNotesLength = 2000
        ReminderNotesInput(
            reminderState = reminderState,
            onNotesChange = { if (it.length <= maxNotesLength) reminderState.notes = it },
        )
    }
}

@Composable
fun ReminderSwitchRow(
    icon: Painter,
    switchText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TextWithLeftIcon(
            icon = icon,
            text = switchText,
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun RepeatIntervalInput(
    reminderState: ReminderState,
    onRepeatAmountChange: (String) -> Unit,
    onRepeatUnitChange: (String) -> Unit
) {
    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0
    val pluralDays = pluralStringResource(R.plurals.radio_button_days, repeatAmount)
    val pluralWeeks = pluralStringResource(R.plurals.radio_button_weeks, repeatAmount)
    val repeatUnitOptions = listOf(pluralDays, pluralWeeks)

    reminderState.repeatUnit = when {
        stringResource(R.string.day) in reminderState.repeatUnit -> pluralDays
        else -> pluralWeeks
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.margin_normal),
                    bottom = dimensionResource(R.dimen.margin_tiny)
                )
        ) {
            Text(
                text = "Repeats every",
                color = colorResource(R.color.subtitle_grey)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .weight(1f)
            ) {
                RepeatAmountInput(
                    reminderState = reminderState,
                    onRepeatAmountChange = onRepeatAmountChange,
                )
            }

            Spacer(Modifier.width(dimensionResource(R.dimen.margin_x_large)))

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .weight(1f)
            ) {
                RepeatUnitInput(
                    reminderState = reminderState,
                    onRepeatUnitChange = onRepeatUnitChange,
                    repeatUnitOptions = repeatUnitOptions,
                )
            }
        }
    }


}

@Composable
fun RepeatAmountInput(
    reminderState: ReminderState,
    onRepeatAmountChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = reminderState.repeatAmount,
        onValueChange = onRepeatAmountChange,
        textStyle = TextStyle(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .width(72.dp)
            .padding(end = dimensionResource(R.dimen.margin_normal))
    )
}

@Composable
fun RepeatUnitInput(
    reminderState: ReminderState,
    onRepeatUnitChange: (String) -> Unit,
    repeatUnitOptions: List<String>,
) {
    Column {
        repeatUnitOptions.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (text == reminderState.repeatUnit),
                        onClick = { onRepeatUnitChange(text) }
                    )
            ) {
                RadioButton(
                    selected = (text == reminderState.repeatUnit),
                    onClick = { onRepeatUnitChange(text) }
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_tiny)))

                Text(
                    text = text
                )
            }
        }
    }
}

@Composable
fun ReminderNotesInput(
    reminderState: ReminderState,
    onNotesChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.margin_small))
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_notes),
            contentDescription = null,
            tint = colorResource(R.color.icon_grey),
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

        RemindMeTextField(
            text = reminderState.notes.orEmpty(),
            onTextChange = onNotesChange,
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
            hintText = stringResource(R.string.hint_reminder_notes),
            imeAction = ImeAction.None,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

private fun sanitiseRepeatAmount(repeatAmount: String): String {
    return repeatAmount
        .trimStart { it == '0' }
        .filter { it.isDigit() }
}

@Preview
@Composable
private fun AddReminderContentPreview() {
    MdcTheme {
        val reminderState by remember { mutableStateOf(ReminderState()) }
        reminderState.isRepeatReminder = true

        AddReminderScaffold(
            reminderState = reminderState,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
