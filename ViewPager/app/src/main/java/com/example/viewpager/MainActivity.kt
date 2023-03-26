package com.example.viewpager

import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.viewpager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnDateSetListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var toggle: ActionBarDrawerToggle
    private val calendar = Calendar.getInstance()
    var startDayOfWeek = "SUN"
    var dateSelected = 0L
    var colorDateSelected = Color.CYAN
    var currentMonthShowing = calendar[Calendar.MONTH]
    var currentYearShowing = calendar[Calendar.YEAR]

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
            R.id.choose_sunday -> {
                startDayOfWeek = "SUN"
            }
            R.id.choose_monday -> {
                startDayOfWeek = "MON"
            }
            R.id.choose_tuesday -> {
                startDayOfWeek = "TUE"
            }
            R.id.choose_wednesday -> {
                startDayOfWeek = "WED"
            }
            R.id.choose_thursday -> {
                startDayOfWeek = "THU"
            }
            R.id.choose_friday -> {
                startDayOfWeek = "FRI"
            }
            R.id.choose_saturday -> {
                startDayOfWeek = "SAT"
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
        supportFragmentManager.fragments.forEach {
            (it as? CalendarFragment)?.selectStartDayOfWeek(startDayOfWeek)
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
                val monthYearPickerDialog = MonthYearPickerDialog()
                monthYearPickerDialog.setDatePickerListener(this)
                monthYearPickerDialog.show(supportFragmentManager, null)
                binding.root.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, date: Int) {
        val startMonth = YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        val endMonth = YearMonth.of(year, month)
        currentMonthShowing = month - 1
        currentYearShowing = year
        val monthsBetween = ChronoUnit.MONTHS.between(startMonth, endMonth)
        binding.viewPager.setCurrentItem((50 + monthsBetween).toInt(), true)

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.root.isDrawerOpen(GravityCompat.START)) {
            binding.root.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}