package dev.shorthouse.remindme.ui.list

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.component.dialog.NotificationPermissionRequester
import dev.shorthouse.remindme.ui.component.dialog.ReminderSortDialog
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateCompletedReminders
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateOverdueReminders
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateSearchReminders
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateUpcomingReminders
import dev.shorthouse.remindme.ui.component.searchbar.RemindMeSearchBar
import dev.shorthouse.remindme.ui.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.ui.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.ui.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.ui.previewprovider.ReminderListProvider
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.util.enums.ReminderAction

@RootNavGraph(start = true)
@Destination
@Composable
fun ReminderListScreen(
    viewModel: ReminderListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    viewModel.initialiseUiState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationPermissionRequester()

    ReminderListScaffold(
        uiState = uiState,
        onApplyFilter = { viewModel.updateReminderFilter(it) },
        onApplySort = { viewModel.updateReminderSortOrder(it) },
        onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
        onSearch = { viewModel.updateIsSearchBarShown(true) },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onCloseSearch = {
            viewModel.updateIsSearchBarShown(false)
            viewModel.updateSearchQuery("")
        },
        onDismissBottomSheet = { viewModel.updateIsBottomSheetShown(false) },
        onReminderCard = { reminder ->
            viewModel.updateBottomSheetReminder(reminder)
            viewModel.updateIsBottomSheetShown(true)
        },
        onReminderActionSelected = { reminderAction ->
            viewModel.updateIsBottomSheetShown(false)

            when (reminderAction) {
                ReminderAction.EDIT -> navigator.navigate(
                    ReminderEditScreenDestination(
                        reminderId = uiState.bottomSheetReminder.id
                    )
                )
                else -> viewModel.processReminderAction(
                    reminderAction = reminderAction,
                    reminder = uiState.bottomSheetReminder.copy()
                )
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReminderListScaffold(
    uiState: ListUiState,
    onApplyFilter: (ReminderFilter) -> Unit,
    onApplySort: (ReminderSort) -> Unit,
    onSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    onNavigateAdd: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onReminderCard: (Reminder) -> Unit,
    onReminderActionSelected: (ReminderAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ReminderListTopBar(
                reminderSortOrder = uiState.reminderSortOrder,
                onApplySort = onApplySort,
                onSearch = onSearch,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onCloseSearch = onCloseSearch,
                isSearchBarShown = uiState.isSearchBarShown
            )
        },
        content = { scaffoldPadding ->
            Column(modifier = Modifier.padding(scaffoldPadding)) {
                if (!uiState.isLoading) {
                    if (!uiState.isSearchBarShown) {
                        ReminderListFilterChips(
                            uiState.reminderFilter,
                            onApplyFilter
                        )
                    }

                    ReminderList(
                        reminders = uiState.reminders,
                        emptyState = {
                            when {
                                uiState.reminders.isNotEmpty() -> {}
                                uiState.isSearchBarShown && uiState.searchQuery.isEmpty() -> {}
                                uiState.isSearchBarShown && uiState.searchQuery.isNotEmpty() ->
                                    EmptyStateSearchReminders()
                                uiState.reminderFilter == ReminderFilter.OVERDUE ->
                                    EmptyStateOverdueReminders()
                                uiState.reminderFilter == ReminderFilter.UPCOMING ->
                                    EmptyStateUpcomingReminders()
                                else -> EmptyStateCompletedReminders()
                            }
                        },
                        onReminderCard = onReminderCard,
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            top = 8.dp,
                            end = 8.dp,
                            bottom = 92.dp
                        )
                    )
                }

                if (uiState.isBottomSheetShown) {
                    BottomSheetReminderActions(
                        reminder = uiState.bottomSheetReminder,
                        onReminderActionSelected = onReminderActionSelected,
                        onDismissRequest = onDismissBottomSheet
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.isSearchBarShown,
                enter = scaleIn(),
                exit = ExitTransition.None
            ) {
                FloatingActionButton(
                    onClick = onNavigateAdd,
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.cd_fab_add_reminder),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListTopBar(
    reminderSortOrder: ReminderSort,
    onApplySort: (ReminderSort) -> Unit,
    onSearch: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    isSearchBarShown: Boolean,
    modifier: Modifier = Modifier
) {
    var isSortDialogOpen by remember { mutableStateOf(false) }

    if (isSortDialogOpen) {
        ReminderSortDialog(
            initialSort = reminderSortOrder,
            onApplySort = onApplySort,
            onDismiss = { isSortDialogOpen = false }
        )
    }

    BackHandler(enabled = isSearchBarShown) {
        onCloseSearch()
    }

    val topBarColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary
    }

    val onTopBarColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    if (isSearchBarShown) {
        RemindMeSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onCloseSearch = onCloseSearch
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge.copy(color = onTopBarColor)
                )
            },
            actions = {
                IconButton(onClick = { isSortDialogOpen = true }) {
                    Icon(
                        imageVector = Icons.Rounded.SwapVert,
                        contentDescription = stringResource(R.string.cd_sort_reminders),
                        tint = onTopBarColor
                    )
                }
                IconButton(onClick = onSearch) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.cd_search_reminders),
                        tint = onTopBarColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = topBarColor
            ),
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListFilterChips(
    selectedReminderFilter: ReminderFilter,
    onApplyFilter: (ReminderFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 8.dp)
    ) {
        ReminderFilter.values().forEach { reminderFilter ->
            ElevatedFilterChip(
                selected = reminderFilter == selectedReminderFilter,
                onClick = {
                    if (reminderFilter != selectedReminderFilter) {
                        onApplyFilter(reminderFilter)
                    }
                },
                label = {
                    Text(text = stringResource(reminderFilter.nameStringId))
                },
                elevation = FilterChipDefaults.elevatedFilterChipElevation(
                    elevation = 4.dp
                ),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    labelColor = MaterialTheme.colorScheme.primary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(
                    horizontal = 4.dp
                )
            )
        }
    }
}

@Composable
fun ReminderList(
    reminders: List<Reminder>,
    emptyState: @Composable () -> Unit,
    onReminderCard: (Reminder) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    if (reminders.isEmpty()) {
        emptyState()
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = contentPadding,
            modifier = modifier
                .fillMaxSize()
                .testTag(stringResource(R.string.test_tag_reminder_list_lazy_column))
        ) {
            items(
                count = reminders.size,
                key = { reminders[it].id },
                itemContent = { index ->
                    ReminderCard(
                        reminder = reminders[index],
                        onReminderCard = onReminderCard
                    )
                }
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReminderListPreview(
    @PreviewParameter(ReminderListProvider::class) reminders: List<Reminder>
) {
    AppTheme {
        ReminderListScaffold(
            uiState = ListUiState(reminders = reminders),
            onApplyFilter = {},
            onApplySort = {},
            onSearch = {},
            onSearchQueryChange = {},
            onCloseSearch = {},
            onNavigateAdd = {},
            onReminderCard = {},
            onDismissBottomSheet = {},
            onReminderActionSelected = {}
        )
    }
}
