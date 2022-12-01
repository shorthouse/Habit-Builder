package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.ReminderDetailsScreen
import dev.shorthouse.remindme.databinding.FragmentDetailsBinding
import dev.shorthouse.remindme.utilities.showToast
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val navigationArgs: DetailsFragmentArgs by navArgs()

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setTransitionAnimations()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false).apply {
            detailsComposeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

                val editAction = DetailsFragmentDirections.actionDetailsToEdit(navigationArgs.id)

                setContent {
                    MdcTheme {
                        ReminderDetailsScreen(
                            detailsViewModel = viewModel,
                            onNavigateEdit = { findNavController().navigate(editAction) },
                            onNavigateUp = { findNavController().navigateUp() },
                        )
                    }
                }
            }
        }

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        //setupToolbar()
//        displayReminderData()
//    }

//    private fun setTransitionAnimations() {
//        val forwardTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
//            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
//            excludeTarget(R.id.app_bar, true)
//        }
//
//        val backwardTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
//            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
//            excludeTarget(R.id.app_bar, true)
//        }
//
//        enterTransition = forwardTransition
//        exitTransition = forwardTransition
//        returnTransition = backwardTransition
//        reenterTransition = backwardTransition
//    }

//    private fun displayReminderData() {
//        viewModel.reminder.observe(viewLifecycleOwner) { reminderValue ->
//            reminderValue?.let {
//                binding.reminder = it
//                //populateReminderData(it)
//            }
//        }
//    }

    //    private fun populateReminderData(reminder: Reminder) {
//        binding.apply {
//            name.text = reminder.name
//            startDate.text = reminder.getFormattedStartDateDayOfWeek()
//            startTime.text = reminder.getFormattedStartTime()
//            notes.text = reminder.notes
//        }
//    }
//
//    private fun setupToolbar() {
//        binding.toolbar.apply {
//            setNavigationOnClickListener { findNavController().navigateUp() }
//            setNavigationIcon(R.drawable.ic_back)
//
//            setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.action_edit -> {
//                        navigateToEditReminder()
//                        true
//                    }
//                    R.id.action_delete -> {
//                        getDeleteAlertDialog().show()
//                        true
//                    }
//                    R.id.action_complete -> {
//                        getCompleteAlertDialog().show()
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
//    }
//
    private fun getCompleteAlertDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.Theme_RemindMe_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.alert_dialog_complete_reminder_message))
            .setPositiveButton(getString(R.string.alert_dialog_complete_reminder_complete)) { dialog, _ ->
                viewModel.completeReminder()
                showToast(R.string.toast_reminder_completed, requireContext())
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.alert_dialog_complete_reminder_cancel)) { dialog, _ ->
                dialog.cancel()
            }
    }

    private fun getDeleteAlertDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.Theme_RemindMe_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.alert_dialog_delete_reminder_message))
            .setPositiveButton(getString(R.string.alert_dialog_delete_reminder_delete)) { dialog, _ ->
                viewModel.deleteReminder()
                showToast(R.string.toast_reminder_deleted, requireContext())
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.alert_dialog_delete_reminder_cancel)) { dialog, _ ->
                dialog.cancel()
            }
    }

    private fun navigateToEditReminder() {
        val action = DetailsFragmentDirections.actionDetailsToEdit(navigationArgs.id)
        findNavController().navigate(action)
    }
}
