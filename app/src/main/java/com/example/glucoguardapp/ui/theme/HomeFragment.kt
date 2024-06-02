package com.example.glucoguardapp.ui.theme

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.glucoguardapp.R
import com.example.glucoguardapp.databinding.FragmentHomeBinding
import com.example.glucoguardapp.helper.showBottomSheet
import com.example.glucoguardapp.ui.theme.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        configTabLayout()

        openCalcInsulin()

        initClicks()

    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener { logoutApp() }
    }

    private fun openCalcInsulin() {
        binding.ibCalc.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_calcInsulinaFragment) }
    }

    private fun logoutApp() {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message = R.string.txt_message_logout,
            onClick = {
                auth.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_authentication)
            })
    }

    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        adapter.addFragment(GlycemiaFragment(), R.string.option_glycemia)
        adapter.addFragment(MedicationFragment(), R.string.option_medication)
        adapter.addFragment(DietFragment(), R.string.option_diet)

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) { tab, position ->
            tab.text = getString(adapter.getTitle(position))
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
