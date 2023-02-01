package dev.shorthouse.remindme.compose.state

import androidx.compose.runtime.*
import dev.shorthouse.remindme.utilities.enums.ReminderBottomSheet
import dev.shorthouse.remindme.utilities.enums.ReminderList
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder

fun ReminderListSheetsState(
    selectedSheet: ReminderBottomSheet,
    selectedReminderListIndex: Int,
    selectedReminderSortOrderIndex: Int,
): ReminderListSheetsState = ReminderListSheetsStateImpl(
    selectedSheet = selectedSheet,
    selectedReminderListIndex = selectedReminderListIndex,
    selectedReminderSortOrderIndex = selectedReminderSortOrderIndex
)

@Stable
interface ReminderListSheetsState {
    var selectedSheet: ReminderBottomSheet
    var selectedReminderListIndex: Int
    val selectedReminderList: ReminderList
    var selectedReminderSortOrderIndex: Int
    val selectedReminderSortOrder: ReminderSortOrder
}

private class ReminderListSheetsStateImpl(
    selectedSheet: ReminderBottomSheet,
    selectedReminderListIndex: Int,
    selectedReminderSortOrderIndex: Int,
) : ReminderListSheetsState {
    private var _selectedSheet by mutableStateOf(selectedSheet)
    override var selectedSheet: ReminderBottomSheet
        get() = _selectedSheet
        set(value) {
            _selectedSheet = value
        }

    private var _selectedReminderListIndex by mutableStateOf(selectedReminderListIndex)
    override var selectedReminderListIndex: Int
        get() = _selectedReminderListIndex
        set(value) {
            _selectedReminderListIndex = value
        }

    private val _selectedReminderList by derivedStateOf(
        policy = structuralEqualityPolicy(),
        calculation = {
            when (_selectedReminderListIndex) {
                0 -> ReminderList.OVERDUE
                1 -> ReminderList.SCHEDULED
                else -> ReminderList.COMPLETED
            }
        }
    )
    override val selectedReminderList: ReminderList
        get() = _selectedReminderList

    private var _selectedReminderSortOrderIndex by mutableStateOf(selectedReminderSortOrderIndex)
    override var selectedReminderSortOrderIndex: Int
        get() = _selectedReminderSortOrderIndex
        set(value) {
            _selectedReminderSortOrderIndex = value
        }

    private val _selectedReminderSortOrder by derivedStateOf(
        policy = structuralEqualityPolicy(),
        calculation = {
            when (_selectedReminderSortOrderIndex) {
                0 -> ReminderSortOrder.EARLIEST_DATE_FIRST
                else -> ReminderSortOrder.LATEST_DATE_FIRST
            }
        }
    )
    override val selectedReminderSortOrder: ReminderSortOrder
        get() = _selectedReminderSortOrder

//    private val _selectedReminderList by derivedStateOf(
//        policy = structuralEqualityPolicy(),
//        calculation = {
//            ReminderList.SCHEDULED
//        }
//    )
//    override val selectedReminderList: ReminderList
//        get() = _selectedReminderList
//        set(value) {
//            _selectedReminderList = value
//        }


//    private var _sortSheetSelectionIndex by mutableStateOf(sortSheetSelectionIndex, structuralEqualityPolicy())
//    override var sortSheetSelectionIndex: Int
//        get() = _sortSheetSelectionIndex
//        set(value) {
//            _sortSheetSelectionIndex = value
//        }

//    private var _reminderSortOrder by derivedStateOf(structuralEqualityPolicy(),
//        when (_sortSheetSelectionIndex) {
//            0 -> _reminderSortOrder = ReminderList.OVERDUE
//            1 -> _reminderSortOrder =  ReminderList.SCHEDULED
//            else -> _reminderSortOrder =  ReminderList.COMPLETED
//        }
//    )
//    override var reminderSortOrder: ReminderSortOrder
//        get() = _reminderSortOrder
//        set(value) {
//            _reminderSortOrder = value
//        }
}
