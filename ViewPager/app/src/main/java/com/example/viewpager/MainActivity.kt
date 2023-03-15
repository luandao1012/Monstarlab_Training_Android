package com.example.viewpager

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.viewpager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var positionViewPager: Int = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(50, false)
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = viewPagerAdapter
    }

    private fun initListeners() {
        binding.spSelectStartDay.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                showFragment()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                positionViewPager = position
                CalendarFragment.startDayOfWeek = binding.spSelectStartDay.selectedItem.toString()
                showFragment()
            }
        })
    }

    private fun showFragment() {
        val calendarFragment =
            supportFragmentManager.findFragmentByTag("f$positionViewPager") as CalendarFragment
        calendarFragment.selectStartDayOfWeek(binding.spSelectStartDay.selectedItem.toString())
    }
}