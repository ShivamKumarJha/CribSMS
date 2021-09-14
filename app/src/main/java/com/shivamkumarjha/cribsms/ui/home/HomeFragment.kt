package com.shivamkumarjha.cribsms.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shivamkumarjha.cribsms.R
import com.shivamkumarjha.cribsms.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    //Views
    private var binding: FragmentHomeBinding? = null

    //ViewModel
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)
        binding = FragmentHomeBinding.bind(view)
        observer()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun observer() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

        }
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            if (!messages.isNullOrEmpty()) {

            }
        }
    }

}