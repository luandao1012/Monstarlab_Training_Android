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
            writer?.write(""""Date","Content"""")
            writer?.newLine()
            listDiary.forEach {
                val date = "${it.date},"
                val content = it.content.replace("\"", "\"\"")
                writer?.write(date + "\"${content}\"")
                writer?.newLine()
            }
            writer?.flush()
            outputStream?.close()
        }
    }

    fun restoreFromCSV(context: Context, uri: Uri) {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val reader = inputStream?.bufferedReader()
        var line: String = reader?.readLine().toString()
        if (line != """"Date","Content"""") {
            Toast.makeText(context, "File không hợp lệ", Toast.LENGTH_SHORT).show()
        } else {
            var date = ""
            var content = ""
            var count = 0
            while (reader?.readLine().also { if (it != null) { line = it } } != null) {
                if (count == 0) {
                    val columns = line.split(",", limit = 2)
                    date = columns[0]
                    content = columns[1]
                } else {
                    content += "\n" + line
                }
                count += line.count { it == '"' }
                if ((count % 2 == 0 && line.last() == '"')) {
                    content = content.substring(1, content.length - 1)
                    content = content.replace("\"\"", "\"")
                    addDiary(Diary(date.toLong(), content))
                    date = ""
                    content = ""
                    count = 0
                }
            }
            inputStream?.close()
        }
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