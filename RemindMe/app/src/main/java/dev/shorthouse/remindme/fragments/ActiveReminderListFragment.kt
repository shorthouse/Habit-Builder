package dev.shorthouse.remindme.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.math.MathUtils
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.remindme.viewmodel.ActiveReminderListViewModel

@AndroidEntryPoint
class ActiveReminderListFragment : Fragment() {
    private lateinit var binding: FragmentActiveReminderListBinding

    private val viewModel: ActiveReminderListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ActiveReminderListAdapter()

        viewModel.activeReminders.observe(this.viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            activeReminderRecycler.adapter = adapter

            addReminderFab.setOnClickListener {
                navigateToReminderDetails()
            }

            bottomAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.search -> {
                        Toast.makeText(context, "Search icon clicked!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }

        setupBottomNavigationDrawer()
    }

    private fun setupBottomNavigationDrawer() {

        binding.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            navigationView.setCheckedItem(R.id.drawer_active_reminders)

            bottomAppBar.setNavigationOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            navigationView.setNavigationItemSelectedListener { menuItem ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                if (menuItem.itemId == R.id.all_reminders) {
                    navigateToAllReminders()
                }
                true
            }

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val baseColor = Color.BLACK
                    // 60% opacity
                    val baseAlpha =
                        ResourcesCompat.getFloat(resources, R.dimen.material_emphasis_medium)
                    // Map slideOffset from [-1.0, 1.0] to [0.0, 1.0]
                    val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
                    val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
                    val color = Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
                    scrim.setBackgroundColor(color)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        scrim.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        scrim.bringToFront()
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        scrim.setOnClickListener(null)
                        activeReminderRecycler.bringToFront()
                    }
                }
            })
        }
    }

    private fun navigateToReminderDetails() {
        val action = ActiveReminderListFragmentDirections
            .actionActiveRemindersToAddEditReminder()
        findNavController().navigate(action)
    }

    private fun navigateToAllReminders() {
        val action = ActiveReminderListFragmentDirections
            .actionActiveRemindersToAllReminders()
        findNavController().navigate(action)
    }
}
