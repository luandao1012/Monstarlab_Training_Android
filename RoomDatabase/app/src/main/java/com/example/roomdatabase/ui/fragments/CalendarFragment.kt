package com.example.roomdatabase.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.roomdatabase.collectFlow
import com.example.roomdatabase.data.DiaryRoomDatabase
import com.example.roomdatabase.databinding.FragmentCalendarBinding
import com.example.roomdatabase.ui.activities.AddDiaryActivity
import com.example.roomdatabase.ui.activities.CalendarActivity
import com.example.roomdatabase.ui.adapters.CalendarAdapter
import com.example.roomdatabase.ui.viewmodels.CalendarViewModel
import com.example.roomdatabase.ui.viewmodels.CalendarViewModelFactory
import java.util.*

@SuppressLint("SetTextI18n")
class CalendarFragment() : Fragment() {
    companion object {
        const val CALENDAR_FRAGMENT_MONTH_KEY = "Calendar fragment month key"
    }

    private lateinit var binding: FragmentCalendarBinding
    private var monthCalendar = Calendar.getInstance()
    private var calendarAdapter: CalendarAdapter? = null
    private val calendarViewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory(
            DiaryRoomDatabase.getDatabase(requireActivity().applicationContext).diaryDao()
        )
    }
    private var month = -1
    private var year = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            monthCalendar.timeInMillis = it.getLong(CALENDAR_FRAGMENT_MONTH_KEY)
        }
        initViews()
        initListeners()
    }

    private fun initViews() {
        month = monthCalendar[Calendar.MONTH]
        year = monthCalendar[Calendar.YEAR]
        calendarAdapter = CalendarAdapter()
        calendarViewModel.addDateOfMonth(month, year)
        binding.rvCalendar.adapter = calendarAdapter
        calendarAdapter?.setCurrentMonth(month)
        binding.tvMonth.text = "Tháng ${month + 1} - $year"
        setDateSelected()
    }

    private fun initListeners() {
        calendarAdapter?.setDoubleClickListener {
            val bundle = Bundle().apply {
                putLong("date", it)
            }
            val intent = Intent(activity, AddDiaryActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        calendarAdapter?.saveDateSelected { date ->
            (activity as? CalendarActivity)?.apply {
                dateSelected = date
                resetALlFragment()
            }
        }

        collectFlow(calendarViewModel.dateFlow) { listDate ->
            calendarAdapter?.setData(listDate)
        }
    }

    private fun setDateSelected() {
        (activity as? CalendarActivity)?.let {
            calendarAdapter?.setDateSelected(
                it.dateSelected
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetFragment() {
        setDateSelected()
        calendarAdapter?.notifyDataSetChanged()
    }
}