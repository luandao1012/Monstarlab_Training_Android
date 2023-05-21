package com.example.roomdatabase.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.roomdatabase.collectFlow
import com.example.roomdatabase.data.NoteRoomDatabase
import com.example.roomdatabase.databinding.FragmentCalendarBinding
import com.example.roomdatabase.ui.activities.AddNoteActivity
import com.example.roomdatabase.ui.activities.CalendarActivity
import com.example.roomdatabase.ui.adapters.CalendarAdapter
import com.example.roomdatabase.ui.adapters.ContentNoteAdapter
import com.example.roomdatabase.ui.viewmodels.CalendarViewModel
import com.example.roomdatabase.ui.viewmodels.CalendarViewModelFactory
import com.example.roomdatabase.ui.viewmodels.NoteViewModel
import com.example.roomdatabase.ui.viewmodels.NoteViewModelFactory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@SuppressLint("SetTextI18n")
class CalendarFragment() : Fragment() {
    companion object {
        const val CALENDAR_FRAGMENT_MONTH_KEY = "CALENDAR_FRAGMENT_MONTH_KEY"
        const val DATE = "DATE"
        const val IS_VIEW_OR_EDIT = "IS_VIEW_OR_EDIT"
    }

    private lateinit var binding: FragmentCalendarBinding
    private var monthCalendar = Calendar.getInstance()
    private var calendarAdapter: CalendarAdapter? = null
    private var contentNoteAdapter: ContentNoteAdapter? = null
    private val calendarViewModel: CalendarViewModel by viewModels {
        CalendarViewModelFactory(
            NoteRoomDatabase.getDatabase(requireActivity().applicationContext).diaryDao()
        )
    }
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            NoteRoomDatabase.getDatabase(requireActivity().applicationContext).diaryDao()
        )
    }

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
        val month = monthCalendar[Calendar.MONTH]
        val year = monthCalendar[Calendar.YEAR]
        calendarAdapter = CalendarAdapter()
        calendarViewModel.addDateOfMonth(month, year)
        binding.rvCalendar.adapter = calendarAdapter
        calendarAdapter?.setCurrentMonth(month)
        binding.tvMonth.text = "Tháng ${month + 1} - $year"
        setDateSelected()
        contentNoteAdapter = ContentNoteAdapter()
        binding.rvContent.adapter = contentNoteAdapter
    }

    private fun initListeners() {
        calendarAdapter?.setDoubleClickListener {
            val bundle = Bundle().apply {
                putString(DATE, Json.encodeToString(it))
                putBoolean(IS_VIEW_OR_EDIT, false)
            }
            val intent = Intent(activity, AddNoteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        calendarAdapter?.setDateSelectedCallback { date ->
            noteViewModel.getAllNoteByDate(date)
            (activity as? CalendarActivity)?.apply {
                dateSelected = date
                resetALlFragment()
            }
        }
        contentNoteAdapter?.setOnClickListener {
            val bundle = Bundle().apply {
                putString(DATE, Json.encodeToString(it))
                putBoolean(IS_VIEW_OR_EDIT, true)
            }
            val intent = Intent(activity, AddNoteActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        collectFlow(calendarViewModel.dateFlow) { listDate ->
            calendarAdapter?.setData(listDate)
            val dateSelected = (activity as? CalendarActivity)?.dateSelected ?: 0
            if ((dateSelected >= listDate.first().date) and (dateSelected <= listDate.last().date)) {
                noteViewModel.getAllNoteByDate(dateSelected)
            }
        }
        collectFlow(noteViewModel.allNote) {
            contentNoteAdapter?.setData(it)
        }
    }

    private fun setDateSelected() {
        (activity as? CalendarActivity)?.let {
            calendarAdapter?.setDateSelected(it.dateSelected)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetFragment() {
        setDateSelected()
        calendarAdapter?.notifyDataSetChanged()
    }
}