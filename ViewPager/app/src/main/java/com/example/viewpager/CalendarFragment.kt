package com.example.viewpager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewpager.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class CalendarFragment() : Fragment() {
    companion object {
        const val CALENDAR_FRAGMENT_MONTH_KEY = "Calendar fragment month key"
        val DAY_OF_WEEK_LIST = arrayListOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    }

    private lateinit var binding: FragmentCalendarBinding
    private var monthCalendar = Calendar.getInstance()
    private var calendarAdapter: CalendarAdapter? = null
    private var dateList = arrayListOf<DateCalendar>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            monthCalendar.timeInMillis = it.getLong(CALENDAR_FRAGMENT_MONTH_KEY)
        }
        initViews()
        initListeners()
    }

    private fun initListeners() {
        calendarAdapter?.setDateSelected {
            (activity as? MainActivity)?.dateSelected = it.first
            (activity as? MainActivity)?.colorDateSelected = it.second
            (activity as? MainActivity)?.resetALlFragment()
        }
    }

    private fun initViews() {
        calendarAdapter = CalendarAdapter()
        binding.rvCalendar.adapter = calendarAdapter
        calendarAdapter?.setData(dateList)
        (activity as? MainActivity)?.startDayOfWeek?.let { selectStartDayOfWeek(it) }
        setDateSelected()
    }

    private fun setDateSelected() {
        (activity as? MainActivity)?.let {
            calendarAdapter?.setDateSelected(
                it.dateSelected,
                it.colorDateSelected
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetFragment() {
        setDateSelected()
        calendarAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectStartDayOfWeek(day: String) {
        dateList.clear()
        addDayOfWeek()
        changeStartDayOfWeek(day)
        addDateOfMonth()
        binding.tvMonth.text =
            SimpleDateFormat("MMMM - yyyy", Locale.ENGLISH).format(monthCalendar.time)
        calendarAdapter?.notifyDataSetChanged()
    }

    private fun addDayOfWeek() {
        DAY_OF_WEEK_LIST.forEachIndexed { index, element ->
            dateList.add(DateCalendar(element, index.toLong(), CalendarAdapter.DAY_OF_WEEK))
        }
    }

    private fun addDateOfMonth() {
        val month = monthCalendar[Calendar.MONTH]
        val year = monthCalendar[Calendar.YEAR]
        val startMonth = Calendar.getInstance()
        startMonth.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        startMonth.time
        startMonth[Calendar.DAY_OF_WEEK] = dateList[0].date.toInt()
        if (startMonth[Calendar.DATE] != 1 && startMonth[Calendar.MONTH] == month)
            startMonth.add(Calendar.DATE, -7)

        while (true) {
            repeat(7) {
                if (startMonth[Calendar.MONTH] == month) {
                    dateList.add(
                        DateCalendar("", startMonth.timeInMillis, CalendarAdapter.DATE_IN_MONTH)
                    )
                } else {
                    dateList.add(
                        DateCalendar("", startMonth.timeInMillis, CalendarAdapter.DATE_NOT_IN_MONTH)
                    )
                }
                startMonth.add(Calendar.DATE, 1)
            }
            if ((startMonth[Calendar.MONTH] > month) || (startMonth[Calendar.YEAR] > year)) {
                break
            }
        }
    }

    private fun changeStartDayOfWeek(startDay: String) {
        val index = dateList.indexOfFirst { it.day == startDay }
        if (index != -1) {
            val list = index.let { dateList.subList(it, 7).toList() }
            dateList.removeAll(list.toSet())
            dateList.addAll(0, list)
        }
    }
}