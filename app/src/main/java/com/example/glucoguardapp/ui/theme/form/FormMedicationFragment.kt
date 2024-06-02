package com.example.glucoguardapp.ui.theme.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.glucoguardapp.R
import com.example.glucoguardapp.databinding.FragmentFormMedicationBinding
import com.example.glucoguardapp.helper.BaseFragment
import com.example.glucoguardapp.helper.FirebaseHelper
import com.example.glucoguardapp.helper.initToolbar
import com.example.glucoguardapp.helper.showBottomSheet
import com.example.glucoguardapp.model.Medication

class FormMedicationFragment : BaseFragment() {

    private val args: FormMedicationFragmentArgs by navArgs()

    private var _binding: FragmentFormMedicationBinding? = null
    private val binding get() = _binding!!

    private lateinit var medication: Medication
    private var newMedication: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.medication.let {
            if (it != null){
                medication = it

                configMedication()
            }
        }
    }

    private fun configMedication() {
        newMedication = false
        binding.textToolbar.text = getString(R.string.text_editing_form_fragment)

        binding.edtDescription.setText(medication.name)
        binding.edtDosage.setText(medication.dosage)
    }

    /*
    private fun setStatus() {
        binding.radioGroup.check(
            when (task.status) {
                0 -> {
                    R.id.rbTodo
                }
                1 -> {
                    R.id.rbDoing
                }
                else -> {
                    R.id.rbDone
                }
            }
        )
    }
*/

    private fun initListeners() {
        binding.btnSave.setOnClickListener { validateData() }

    }


    private fun validateData() {
        val name = binding.edtDescription.text.toString().trim()
        val dosage = binding.edtDosage.text.toString().trim()

        if (name.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newMedication) medication = Medication()
            medication.name = name
            medication.dosage = dosage
            //task.status = statusTask

            saveMedication()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_fragment)
        }
    }

    private fun saveMedication() {
        FirebaseHelper
            .getDatabase()
            .child("medication")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(medication.id)
            .setValue(medication)
            .addOnCompleteListener { medication ->
                if (medication.isSuccessful) {
                    if (newMedication) { // New Task
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            R.string.text_save_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // Edit Task
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.text_update_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.text_error_save_form_fragment, Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), R.string.text_error_save_form_fragment, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}