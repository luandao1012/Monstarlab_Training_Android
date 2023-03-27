package com.example.viewpager

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.viewpager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private val calendar = Calendar.getInstance()
    var startDayOfWeek = "SUN"
    var dateSelected = 0L
    var colorDateSelected = Color.CYAN
    private var currentMonthShowing = calendar[Calendar.MONTH] + 1
    private var currentYearShowing = calendar[Calendar.YEAR]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            setCurrentItem(50, false)
            offscreenPageLimit = 1
        }
        binding.navDrawer.bringToFront()
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.root,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()
        setDateSelectedIsToday()
    }

    private fun initListeners() {
        binding.navDrawer.setNavigationItemSelectedListener(this)
        binding.drawerExit.setOnClickListener { finish() }
        binding.root.addDrawerListener(toggle)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.root.isDrawerOpen(GravityCompat.START)) {
                    binding.root.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setDateSelectedIsToday() {
        calendar.apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dateSelected = calendar.timeInMillis
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_choose_day -> {
                val subMenuDay = item.subMenu
                for (i in 0 until subMenuDay!!.size()) {
                    subMenuDay.getItem(i).setOnMenuItemClickListener {
                        startDayOfWeek = CalendarFragment.DAY_OF_WEEK_LIST[i]
                        supportFragmentManager.fragments.forEach {
                            (it as? CalendarFragment)?.selectStartDayOfWeek(startDayOfWeek)
                        }
                        true
                    }
                }
            }
            R.id.menu_setting -> {
                Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_current_date -> {
                binding.viewPager.setCurrentItem(50, true)
                setDateSelectedIsToday()
                resetALlFragment()
            }
        }
        return true
    }

    fun resetALlFragment() {
        supportFragmentManager.fragments.forEach {
            (it as? CalendarFragment)?.resetFragment()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_choose_month -> {
                showMonthYearPicker(currentMonthShowing, currentYearShowing) { month, year ->
                    currentMonthShowing = month
                    currentYearShowing = year
                    val startMonth =
                        YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
                    val endMonth = YearMonth.of(year, month)
                    val monthsBetween = ChronoUnit.MONTHS.between(startMonth, endMonth)
                    binding.viewPager.setCurrentItem((50 + monthsBetween).toInt(), true)
                }
                binding.root.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }
}