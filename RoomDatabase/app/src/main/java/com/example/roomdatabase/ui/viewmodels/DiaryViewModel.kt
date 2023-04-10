package com.example.roomdatabase.ui.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.data.Diary
import com.example.roomdatabase.data.DiaryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter

class DiaryViewModel(private val dao: DiaryDao) : ViewModel() {
    var allDiary = MutableSharedFlow<List<Diary>>(replay = 1)
    var currentDiary = MutableSharedFlow<Diary?>(replay = 1)

    fun getAllDiary() {
        viewModelScope.launch {
            dao.getFlowAllDiary().collect {
                allDiary.emit(it)
            }
        }
    }

    fun search(word: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.search(word)
            allDiary.emit(list)
        }
    }

    fun backupToCSV(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver
            val listDiary = dao.getListAllDiary()
            val outputStream = contentResolver.openOutputStream(uri)
            val writer = outputStream?.bufferedWriter()
            val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Date", "Content"))
            for (diary in listDiary) {
                val diaryData = listOf(
                    diary.date,
                    diary.content
                )
                csvPrinter.printRecord(diaryData);
            }
            csvPrinter.flush();
            csvPrinter.close();
            outputStream?.close()
        }
    }

    fun restoreFromCSV(context: Context, uri: Uri) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val reader = inputStream?.bufferedReader()
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withHeader())
        val header = arrayOf("Date", "Content")
        if (csvParser.headerMap.keys.toTypedArray() contentEquals header) {
            for (data in csvParser) {
                val date = data.get("Date")
                val content = data.get("Content")
                addDiary(Diary(date.toLong(), content))
            }
            Toast.makeText(context, "Restore thành công", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "File không hợp lệ", Toast.LENGTH_SHORT).show()
        }
        inputStream?.close()
    }

    fun getDiary(date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentDiary.emit(dao.getDiary(date))
        }
    }

    fun addDiary(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        dao.insertDiary(diary)
    }

    fun updateDiary(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        dao.updateDiary(diary)
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        dao.deleteDiary(diary)
    }
}

class DiaryViewModelFactory(private val dao: DiaryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}