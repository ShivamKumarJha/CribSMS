package com.shivamkumarjha.cribsms.ui.home

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shivamkumarjha.cribsms.R
import com.shivamkumarjha.cribsms.databinding.FragmentHomeBinding
import com.shivamkumarjha.cribsms.ui.extensions.hideKeyboard
import com.shivamkumarjha.cribsms.ui.extensions.toast

class HomeFragment : Fragment(R.layout.fragment_home) {
    //Views
    private var binding: FragmentHomeBinding? = null

    //ViewModel
    private val viewModel: HomeViewModel by viewModels()

    //Result
    private val readSMSPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                viewModel.getSMS(requireActivity().contentResolver)
            } else {
                requireContext().toast(getString(R.string.sms_read_permission_denied))
            }
        }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)
        binding = FragmentHomeBinding.bind(view)
        setViews()
        observer()
        requestSMSPermission()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setViews() {
        binding?.root?.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun observer() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.submitProgressBar?.isVisible = isLoading
        }
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            if (!messages.isNullOrEmpty()) {
            }
        }
    }

    private fun requestSMSPermission() {
        readSMSPermission.launch(Manifest.permission.READ_SMS)
    }

}