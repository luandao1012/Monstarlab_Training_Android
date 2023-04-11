package com.example.roomdatabase.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.data.Diary
import com.example.roomdatabase.data.DiaryDao
import com.example.roomdatabase.setTimeCalendar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.*

class CalendarViewModel(private val dao: DiaryDao) : ViewModel() {
    var dateFlow = MutableSharedFlow<List<Diary>>(replay = 1)

    fun addDateOfMonth(month: Int, year: Int) {
        val dateList = arrayListOf<Diary>()
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
                        dateList.add(Diary(startMonth.timeInMillis, ""))
                    } else {
                        dateList.add(Diary(startMonth.timeInMillis, ""))
                    }
                    startMonth.add(Calendar.DATE, 1)
                }
                if ((startMonth[Calendar.MONTH] > month) || (startMonth[Calendar.YEAR] > year)) {
                    break
                }
            }

            dao.getDiaryInMonth(dateList.first().date, dateList.last().date).collect { listDiary ->
                dateList.forEach { it.content = "" }
                listDiary.forEach { diary ->
                    dateList.firstOrNull { it.date == diary.date }?.content = diary.content
                }
                dateFlow.emit(dateList)
            }
        }
    }
}

class CalendarViewModelFactory(private val dao: DiaryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}