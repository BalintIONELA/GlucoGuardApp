package com.example.glucoguardapp.ui.theme

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.glucoguardapp.R
import com.example.glucoguardapp.databinding.FragmentCalcInsulinBinding
import com.example.glucoguardapp.model.InsulinCalculator
import java.text.NumberFormat
import java.util.*

class CalcInsulinaFragment : Fragment() {
    private var _binding: FragmentCalcInsulinBinding? = null
    private val binding get() = _binding!!

    private lateinit var editWeight: EditText
    private lateinit var editCarbohydrate: EditText
    private lateinit var btnCalculate: Button
    private lateinit var txtResultUI: TextView
    private lateinit var icResetData: ImageView
    private lateinit var btnReminder: Button
    private lateinit var btnAlarm: Button
    private lateinit var txtHour: TextView
    private lateinit var txtMinutes: TextView

    private lateinit var calculateInsulin: InsulinCalculator
    private var result = 0.0

    lateinit var timePickerDialog: TimePickerDialog
    lateinit var calendar: Calendar
    var currentHour = 0
    var currentMinutes = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalcInsulinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeComponents()
        calculateInsulin = InsulinCalculator()

        btnCalculate.setOnClickListener {
            if (editWeight.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), R.string.toast_inform_weight, Toast.LENGTH_SHORT)
                    .show()
            } else if (editCarbohydrate.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.toast_inform_carbohydrates,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val weight = editWeight.text.toString().toDouble()
                val carbohydrate = editCarbohydrate.text.toString().toDouble()
                calculateInsulin.calculate(weight, carbohydrate)
                result = calculateInsulin.result()
                val formatter = NumberFormat.getNumberInstance(Locale("pt", "BR"))
                formatter.isGroupingUsed = false
                txtResultUI.text = formatter.format(result) + " UI"
            }
        }

        icResetData.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_desc)
                .setPositiveButton("Ok") { _, _ ->
                    editWeight.setText("")
                    editCarbohydrate.setText("")
                    txtResultUI.text = ""
                }
                .setNegativeButton("Cancel") { _, _ ->
                    // Cancel action
                }
            val dialog = alertDialog.create()
            dialog.show()
        }

        btnReminder.setOnClickListener {
            calendar = Calendar.getInstance()
            currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            currentMinutes = calendar.get(Calendar.MINUTE)
            timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minutes ->
                    txtHour.text = String.format("%02d", hourOfDay)
                    txtMinutes.text = String.format("%02d", minutes)
                },
                currentHour,
                currentMinutes,
                true
            )
            timePickerDialog.show()
        }

        btnAlarm.setOnClickListener {
            if (!txtHour.text.toString().isEmpty() && !txtMinutes.text.toString().isEmpty()) {
                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                intent.putExtra(AlarmClock.EXTRA_HOUR, txtHour.text.toString().toInt())
                intent.putExtra(AlarmClock.EXTRA_MINUTES, txtMinutes.text.toString().toInt())
                intent.putExtra(
                    AlarmClock.EXTRA_MESSAGE,
                    getString(R.string.alarm_message) + " Your dosage is: " + txtResultUI.text
                )
                startActivity(intent)
            }
        }

        binding.logoEmDBack.setOnClickListener {
            backHome()
        }
    }

    private fun backHome() {
        findNavController().navigate(R.id.action_calcInsulinaFragment_to_homeFragment)
    }

    private fun initializeComponents() {
        editWeight = binding.editWeight
        editCarbohydrate = binding.editCarbohydrates
        btnCalculate = binding.btCalculate
        txtResultUI = binding.txtResultUi
        icResetData = binding.icRedefine
        btnAlarm = binding.btAlarm
        btnReminder = binding.btDefineReminder
        txtHour = binding.txtHour
        txtMinutes = binding.txtMinutes

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
