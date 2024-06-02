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
import com.example.glucoguardapp.databinding.FragmentFormGlycemiaBinding
import com.example.glucoguardapp.helper.BaseFragment
import com.example.glucoguardapp.helper.FirebaseHelper
import com.example.glucoguardapp.helper.initToolbar
import com.example.glucoguardapp.helper.showBottomSheet
import com.example.glucoguardapp.model.Glycemia

class FormGlycemiaFragment : BaseFragment() {

    private val args: FormGlycemiaFragmentArgs by navArgs()

    private var _binding: FragmentFormGlycemiaBinding? = null
    private val binding get() = _binding!!

    private lateinit var glycemia: Glycemia
    private var newGlycemia: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormGlycemiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.glycemia.let {
            if (it != null){
                glycemia = it

                configGlycemia()
            }
        }
    }

    private fun configGlycemia() {
        newGlycemia = false

        binding.textToolbar.text = getString(R.string.text_editing_form_fragment)

        binding.edtDescription.setText(glycemia.description)
        binding.edtGlycemia.setText(glycemia.glucoseLevel)
        binding.edtDay.setText(glycemia.day.toString())
        binding.edtMonth.setText(glycemia.month.toString())
        binding.edtYear.setText(glycemia.year.toString())
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
        //blood glucose quantity
        val glicemia = binding.edtGlycemia.text.toString().trim()
        val description = binding.edtDescription.text.toString().trim()
        val day = binding.edtDay.text.toString().toInt()
        val month = binding.edtMonth.text.toString().toInt()
        val yaer = binding.edtYear.text.toString().toInt()

        if (glicemia.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newGlycemia) glycemia= Glycemia()
            glycemia.glucoseLevel = glicemia
            glycemia.description = description
            glycemia.day = day
            glycemia.month = month
            glycemia.year = yaer

            saveGlycemia()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_fragment)
        }
    }

    private fun saveGlycemia() {
        FirebaseHelper
            .getDatabase()
            .child("glycemia")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(glycemia.id)
            .setValue(glycemia)
            .addOnCompleteListener { glycemia ->
                if (glycemia.isSuccessful) {
                    if (newGlycemia) { // New Task
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            R.string.text_save_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // Editando tarefa
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