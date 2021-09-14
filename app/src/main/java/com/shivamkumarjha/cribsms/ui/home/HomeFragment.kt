package com.shivamkumarjha.cribsms.ui.home

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shivamkumarjha.cribsms.R
import com.shivamkumarjha.cribsms.databinding.FragmentHomeBinding
import com.shivamkumarjha.cribsms.ui.extensions.afterTextChanged
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
                val daysGap = daysInput()
                if (daysGap != null) {
                    viewModel.getSMS(requireActivity().contentResolver, phoneInput(), daysGap)
                }
            } else {
                requireContext().toast(getString(R.string.sms_read_permission_denied))
            }
        }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)
        binding = FragmentHomeBinding.bind(view)
        setViews()
        observer()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setViews() {
        binding?.root?.setOnClickListener {
            hideKeyboard()
        }
        binding?.phoneEditText?.afterTextChanged {
            validateInput()
        }
        binding?.daysEditText?.afterTextChanged {
            validateInput()
        }
        binding?.submitButton?.setOnClickListener {
            requestSMSPermission()
        }
    }

    private fun observer() {
        viewModel.formState.observe(viewLifecycleOwner) { formState ->
            if (formState != null) {
                binding?.submitButton?.isEnabled = formState.isInputValid

                val phoneText = binding?.phoneEditText?.text.toString().trim()
                if (formState.phoneError != null && phoneText.isNotEmpty()) {
                    binding?.phoneLayout?.error = getString(formState.phoneError)
                } else {
                    binding?.phoneLayout?.error = null
                }

                val daysText = binding?.daysEditText?.text.toString().trim()
                if (formState.daysError != null && daysText.isNotEmpty()) {
                    binding?.daysLayout?.error = getString(formState.daysError)
                } else {
                    binding?.daysLayout?.error = null
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.submitProgressBar?.isVisible = isLoading
        }
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            if (!messages.isNullOrEmpty()) {
                requireContext().toast(messages.size.toString())
                messages.forEach {
                    Log.d("ZXCVB", "${it.time} ${it.address} ${it.folder}")
                }
            }
        }
    }

    private fun validateInput() {
        viewModel.inputValidator(phoneInput(), daysInput())
    }

    private fun phoneInput(): String = binding?.phoneEditText?.text?.toString()?.trim() ?: ""

    private fun daysInput(): Int? = binding?.daysEditText?.text?.toString()?.trim()?.toIntOrNull()

    private fun requestSMSPermission() {
        readSMSPermission.launch(Manifest.permission.READ_SMS)
    }

}