package com.example.viewpager

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.example.viewpager.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    var startDayOfWeek = "SUN"
    var dateSelected = 0L
    var colorDateSelected = Color.CYAN

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
        setDateSelectedIsToday()
    }

    private fun setDateSelectedIsToday() {
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dateSelected = calendar.timeInMillis
    }

    private fun initListeners() {
        binding.spSelectStartDay.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                startDayOfWeek = binding.spSelectStartDay.selectedItem.toString()
                supportFragmentManager.fragments.forEach {
                    (it as? CalendarFragment)?.selectStartDayOfWeek(startDayOfWeek)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
        binding.btCurrentDate.setOnClickListener {
            binding.viewPager.setCurrentItem(50, true)
            setDateSelectedIsToday()
            resetALlFragment()
        }
    }

    fun resetALlFragment() {
        supportFragmentManager.fragments.forEach {
            (it as? CalendarFragment)?.resetFragment()
        }
    }
}