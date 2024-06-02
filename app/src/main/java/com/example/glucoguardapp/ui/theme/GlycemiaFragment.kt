package com.example.glucoguardapp.ui.theme

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glucoguardapp.R
import com.example.glucoguardapp.databinding.FragmentGlycemiaBinding
import com.example.glucoguardapp.helper.FirebaseHelper
import com.example.glucoguardapp.helper.showBottomSheet
import com.example.glucoguardapp.model.Glycemia
import com.example.glucoguardapp.ui.theme.adapter.GlycemiaAdapter
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class GlycemiaFragment : Fragment() {

    private var _binding: FragmentGlycemiaBinding? = null
    private val binding get() = _binding!!

    private lateinit var glycemiaAdapter: GlycemiaAdapter

    private val glycemiaList = mutableListOf<Glycemia>()

    lateinit var timePickerDialog: TimePickerDialog
    lateinit var calendar: Calendar
    var currentHour = 0
    var currentMinutes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGlycemiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        initClicks()
        getGlycemias()
        initSearchView()
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                binding.textInfo.text = if (glycemiaAdapter.searchGlycemia(query)) {
                    "No results found"
                } else {
                    ""
                }
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        binding.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewShown() {}

            override fun onSearchViewClosed() {
                glycemiaAdapter.clearSearchGlycemia()
                binding.textInfo.text = ""
            }

            override fun onSearchViewShownAnimation() {}

            override fun onSearchViewClosedAnimation() {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) {
            // Handle search action
        } else {
            // Handle other menu items
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.menu_search)
        binding.searchView.setMenuItem(item)
    }

    private fun initClicks() {
        binding.fabAddGlycemia.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormGlycemiaFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getGlycemias() {
        FirebaseHelper
            .getDatabase()
            .child("glycemia")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        glycemiaList.clear()
                        for (snap in snapshot.children) {
                            val glycemia = snap.getValue(Glycemia::class.java) as Glycemia
                            glycemiaList.add(glycemia)
                        }

                        glycemiaList.reverse()
                        initAdapter()
                    }

                    glycemiasEmpty()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun glycemiasEmpty() {
        binding.textInfo.text = if (glycemiaList.isEmpty()) {
            getText(R.string.text_glycemia_list_empty_fragment)
        } else {
            ""
        }
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        glycemiaAdapter = GlycemiaAdapter(requireContext(), glycemiaList) { glycemia, select ->
            optionSelect(glycemia, select)
        }
        binding.rvTask.adapter = glycemiaAdapter
    }

    private fun alarm(glycemia: Glycemia) {
        calendar = Calendar.getInstance()
        currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        currentMinutes = calendar.get(Calendar.MINUTE)
        timePickerDialog = TimePickerDialog(requireContext(), { timePicker: TimePicker, hourOfDay: Int, minutes: Int ->
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_HOUR, hourOfDay)
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Time to measure glycemia! Chosen Glycemia: ${glycemia.glucoseLevel} mg/dL")
            startActivity(intent)
        }, currentHour, currentMinutes, true)
        timePickerDialog.show()
    }

    private fun optionSelect(glycemia: Glycemia, select: Int) {
        when (select) {
            GlycemiaAdapter.SELECT_REMOVE -> {
                deleteGlycemia(glycemia)
            }
            GlycemiaAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormGlycemiaFragment(glycemia)
                findNavController().navigate(action)
            }
            GlycemiaAdapter.SELECT_DETAILS -> {
                alarm(glycemia)
            }
        }
    }

    private fun updateGlycemia(glycemia: Glycemia) {
        FirebaseHelper
            .getDatabase()
            .child("glycemia")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(glycemia.id)
            .setValue(glycemia)
            .addOnCompleteListener { glycemia ->
                if (glycemia.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_task_update_sucess,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showBottomSheet(message = R.string.error_generic)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottomSheet(message = R.string.error_generic)
            }
    }

    private fun deleteGlycemia(glycemia: Glycemia) {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message = R.string.text_message_delete_glycemia_fragment,
            onClick = {
                FirebaseHelper
                    .getDatabase()
                    .child("glycemia")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(glycemia.id)
                    .removeValue()
                    .addOnCompleteListener { glycemia ->
                        if (glycemia.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                R.string.text_task_update_sucess,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showBottomSheet(message = R.string.error_generic)
                        }
                    }.addOnFailureListener {
                        binding.progressBar.isVisible = false
                        showBottomSheet(message = R.string.error_generic)
                    }

                glycemiaList.remove(glycemia)
                glycemiaAdapter.notifyDataSetChanged()

                Toast.makeText(
                    requireContext(),
                    R.string.text_task_delete_sucess,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
