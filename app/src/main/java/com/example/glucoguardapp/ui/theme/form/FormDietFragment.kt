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
import com.example.glucoguardapp.databinding.FragmentFormDietBinding
import com.example.glucoguardapp.helper.BaseFragment
import com.example.glucoguardapp.helper.FirebaseHelper
import com.example.glucoguardapp.helper.initToolbar
import com.example.glucoguardapp.helper.showBottomSheet
import com.example.glucoguardapp.model.Diet

class FormDietFragment : BaseFragment() {

    private val args: FormDietFragmentArgs by navArgs()

    private var _binding: FragmentFormDietBinding? = null
    private val binding get() = _binding!!

    private lateinit var diet: Diet
    private var newDiet: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormDietBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.diet.let {
            if (it != null) {
                diet = it
                configDiet()
            }
        }
    }

    private fun configDiet() {
        newDiet = false
        binding.textToolbar.text = getString(R.string.text_editing_form_fragment)

        binding.edtDescription.setText(diet.food)
        binding.edtFood.setText(diet.food)
    }

    private fun initListeners() {
        binding.btnSave.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val meal = binding.edtDescription.text.toString().trim()
        val food = binding.edtFood.text.toString().trim()

        if (meal.isNotEmpty()) {
            hideKeyboard()
            binding.progressBar.isVisible = true

            if (newDiet) diet = Diet()
            diet.meal = meal
            diet.food = food

            saveDiet()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_fragment)
        }
    }

    private fun saveDiet() {
        FirebaseHelper
            .getDatabase()
            .child("diet")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(diet.id)
            .setValue(diet)
            .addOnCompleteListener { contact ->
                if (contact.isSuccessful) {
                    if (newDiet) {
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            R.string.text_save_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.text_update_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_error_save_form_fragment,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(
                    requireContext(),
                    R.string.text_error_save_form_fragment,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
