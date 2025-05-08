package com.irosinfo.feature.home.presentation

import android.os.Bundle
import android.view.View
import com.bases.bases_module.base_fragment.BaseFragment
import com.irosinfo.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun layoutInflater() = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}