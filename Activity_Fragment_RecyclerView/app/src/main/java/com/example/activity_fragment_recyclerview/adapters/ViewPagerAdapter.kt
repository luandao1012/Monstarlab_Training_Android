package com.example.activity_fragment_recyclerview.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.activity_fragment_recyclerview.fragments.onboarding.OnBoardingFragment1
import com.example.activity_fragment_recyclerview.fragments.onboarding.OnBoardingFragment2
import com.example.activity_fragment_recyclerview.fragments.onboarding.OnBoardingFragment3

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingFragment1()
            1 -> OnBoardingFragment2()
            else -> OnBoardingFragment3()
        }
    }
}