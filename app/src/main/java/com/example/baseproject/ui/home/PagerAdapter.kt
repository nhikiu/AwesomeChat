package com.example.baseproject.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.baseproject.models.FragmentData

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val fragmentList: List<FragmentData>)
    : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].fragment
    }
}