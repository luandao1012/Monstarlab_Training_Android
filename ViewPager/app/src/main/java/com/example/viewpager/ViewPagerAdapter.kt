package com.example.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class ViewPagerAdapter(
    fragment: FragmentActivity
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 100

    override fun createFragment(position: Int): Fragment {
        val calendarFragment = CalendarFragment()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, (position - 50))
        calendarFragment.arguments = Bundle().apply {
            putLong(CalendarFragment.CALENDAR_FRAGMENT_MONTH_KEY, calendar.timeInMillis)
        }
        return calendarFragment
    }
}