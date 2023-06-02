package com.example.musicapplication.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapplication.ui.fragments.OfflineFragment
import com.example.musicapplication.ui.fragments.FavouriteFragment
import com.example.musicapplication.ui.fragments.HomeFragment
import com.example.musicapplication.ui.fragments.SearchFragment

class ViewPagerAdapter(
    fragment: FragmentActivity
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> SearchFragment()
            2 -> FavouriteFragment()
            else -> OfflineFragment()
        }
    }
}