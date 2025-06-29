package com.example.prediksikualitasair

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.prediksikualitasair.databinding.FragmentModelBinding
import com.google.android.material.tabs.TabLayoutMediator

class ModelFragment : Fragment() {

    private var _binding: FragmentModelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnToolbarBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val modelPagerAdapter = ModelPagerAdapter(this)
        binding.viewPager.adapter = modelPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Arsitektur"
                1 -> "Proses ML"
                2 -> "Akurasi"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}