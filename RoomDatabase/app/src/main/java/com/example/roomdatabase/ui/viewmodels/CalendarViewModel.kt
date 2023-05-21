package com.example.roomdatabase.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.data.NoteDao
import com.example.roomdatabase.data.Type
import com.example.roomdatabase.setTimeCalendar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.*

class CalendarViewModel(private val dao: NoteDao) : ViewModel() {
    var dateFlow = MutableSharedFlow<List<Note>>(replay = 1)

    fun addDateOfMonth(month: Int, year: Int) {
        val dateList = arrayListOf<Note>()
        viewModelScope.launch {
            val startMonth = Calendar.getInstance()
            startMonth.apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            startMonth.setTimeCalendar()
            startMonth.time
            startMonth[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY
            if (startMonth[Calendar.DATE] != 1 && startMonth[Calendar.MONTH] == month)
                startMonth.add(Calendar.DATE, -7)

            while (true) {
                repeat(7) {
                    if (startMonth[Calendar.MONTH] == month) {
                        dateList.add(
                            Note(
                                date = startMonth.timeInMillis,
                                content = "",
                                type = Type.DIARY
                            )
                        )
                    } else {
                        dateList.add(
                            Note(
                                date = startMonth.timeInMillis,
                                content = "",
                                type = Type.DIARY
                            )
                        )
                    }
                    startMonth.add(Calendar.DATE, 1)
                }
                if ((startMonth[Calendar.MONTH] > month) || (startMonth[Calendar.YEAR] > year)) {
                    break
                }
            }

            dao.getNoteInMonth(dateList.first().date, dateList.last().date).collect { listDiary ->
                dateList.forEach { it.content = "" }
                listDiary.forEach { diary ->
                    dateList.firstOrNull { it.date == diary.date }?.content = diary.content
                }
                dateFlow.emit(dateList)
            }
        }
    }
}

class CalendarViewModelFactory(private val dao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}