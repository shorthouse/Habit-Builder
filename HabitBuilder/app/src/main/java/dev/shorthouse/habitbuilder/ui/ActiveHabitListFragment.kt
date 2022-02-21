package dev.shorthouse.habitbuilder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.ActiveHabitListFragmentBinding
import dev.shorthouse.habitbuilder.ui.adapter.ReminderListAdapter
import dev.shorthouse.habitbuilder.ui.viewmodel.ActiveReminderListViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.ActiveReminderListViewModelFactory

class ActiveHabitListFragment : Fragment() {
    private var _binding: ActiveHabitListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActiveReminderListViewModel by activityViewModels {
        ActiveReminderListViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActiveHabitListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReminderListAdapter { reminder ->
            /*
            val action = ForageableListFragmentDirections
                .actionForageableListFragmentToForageableDetailFragment(forageable.id)

            findNavController().navigate(action)
             */
        }

        viewModel.reminders.observe(this.viewLifecycleOwner) {reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            activeHabitRecycler.adapter = adapter
            addHabitFab.setOnClickListener {
                findNavController().navigate(R.id.action_activeHabitListFragment_to_addHabitFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}