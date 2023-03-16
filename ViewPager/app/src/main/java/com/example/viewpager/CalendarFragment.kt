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
            (activity as MainActivity).dateSelected = it.first
            (activity as MainActivity).colorDateSelected = it.second
            getDateSelected()
        }
        calendarAdapter?.resetDateSelected((activity as MainActivity)::resetALlFragment)
    }

    private fun initViews() {
        calendarAdapter = CalendarAdapter()
        binding.rvCalendar.adapter = calendarAdapter
        calendarAdapter?.setData(dateList)
        selectStartDayOfWeek((activity as MainActivity).startDayOfWeek)
        getDateSelected()
    }

    private fun getDateSelected() {
        calendarAdapter?.getDateSelected(
            (activity as MainActivity).dateSelected,
            (activity as MainActivity).colorDateSelected
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectStartDayOfWeek(day: String) {
        dateList.clear()
        addDayOfWeek()
        changeStartDayOfWeek(day)
        addDateOfMonth(monthCalendar.get(Calendar.MONTH), monthCalendar.get(Calendar.YEAR))
        binding.tvMonth.text =
            SimpleDateFormat("MMMM - yyyy", Locale.ENGLISH).format(monthCalendar.time)
        getDateSelected()
        calendarAdapter?.notifyDataSetChanged()
    }

    private fun addDayOfWeek() {
        dateList.apply {
            add(DateCalendar("SUN", 1, CalendarAdapter.DAY_OF_WEEK))
            add(DateCalendar("MON", 2, CalendarAdapter.DAY_OF_WEEK))
            add(DateCalendar("TUE", 3, CalendarAdapter.DAY_OF_WEEK))
            add(DateCalendar("WED", 4, CalendarAdapter.DAY_OF_WEEK))
            add(DateCalendar("THU", 5, CalendarAdapter.DAY_OF_WEEK))
            add(DateCalendar("FRI", 6, CalendarAdapter.DAY_OF_WEEK))
            add(DateCalendar("SAT", 7, CalendarAdapter.DAY_OF_WEEK))
        }
    }

    private fun addDateOfMonth(month: Int, year: Int) {
        val startMonth = Calendar.getInstance()
        val endMonth = Calendar.getInstance()
        startMonth.set(Calendar.MONTH, month)
        startMonth.set(Calendar.YEAR, year)
        startMonth.set(Calendar.DAY_OF_MONTH, 1)
        startMonth.set(Calendar.HOUR, 0)
        startMonth.set(Calendar.MINUTE, 0)
        startMonth.set(Calendar.SECOND, 0)
        startMonth.set(Calendar.MILLISECOND, 0)
        startMonth.time
        startMonth[Calendar.DAY_OF_WEEK] = dateList[0].date.toInt()
        if (startMonth[Calendar.DATE] != 1 && startMonth[Calendar.MONTH] == month)
            startMonth.add(Calendar.DATE, -7)

        endMonth.set(Calendar.MONTH, month + 1)
        endMonth.set(Calendar.YEAR, year)
        endMonth.set(Calendar.DAY_OF_MONTH, 0)
        endMonth.time
        endMonth[Calendar.DAY_OF_WEEK] = dateList[6].date.toInt()
        if (endMonth[Calendar.MONTH] == month &&
            endMonth[Calendar.DATE] < monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        ) {
            endMonth.add(Calendar.DATE, 7)
        }
        while (startMonth <= endMonth) {
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
    }

    private fun changeStartDayOfWeek(startDay: String) {
        val index = dateList.indices.find { dateList[it].day == startDay }
        val list = index?.let { dateList.subList(it, 7).toList() }
        if (list != null) {
            dateList.removeAll(list)
            dateList.addAll(0, list)
        }
    }
}