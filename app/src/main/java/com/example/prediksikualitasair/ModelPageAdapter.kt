package com.example.prediksikualitasair

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ModelPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ArchitectureFragment()
            1 -> ProcessFragment()
            2 -> AccuracyFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}