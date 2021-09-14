package com.shivamkumarjha.cribsms.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.shivamkumarjha.cribsms.R
import com.shivamkumarjha.cribsms.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    //Views
    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)
        binding = FragmentHomeBinding.bind(view)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}