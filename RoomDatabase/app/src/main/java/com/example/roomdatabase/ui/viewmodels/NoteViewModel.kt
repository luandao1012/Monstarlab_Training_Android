package com.example.roomdatabase.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.data.NoteDao
import com.example.roomdatabase.data.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NoteViewModel(private val dao: NoteDao) : ViewModel() {
    var allNote = MutableSharedFlow<List<Note>>(replay = 1)
    var currentNote = MutableSharedFlow<Note?>(replay = 1)

    fun getAllDiary() {
        viewModelScope.launch {
            dao.getFlowAllNote().collect {
                allNote.emit(it)
            }
        }
    }

    fun getAllNoteByDate(date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAllNoteByDate(date).collect {
                allNote.emit(it)
            }
        }
    }

    fun getALlNoteByType(type: Type) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAllNoteByType(type).collect {
                allNote.emit(it)
            }
        }
    }

    fun search(word: String, type: Type) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.search(word, type)
            allNote.emit(list)
        }
    }

//    fun backupToCSV(context: Context, uri: Uri) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val contentResolver: ContentResolver = context.contentResolver
//            val listDiary = dao.getListAllDiary()
//            val outputStream = contentResolver.openOutputStream(uri)
//            val writer = outputStream?.bufferedWriter()
//            writer?.write(""""Date","Content"""")
//            writer?.newLine()
//            listDiary.forEach {
//                val date = "${it.date},"
//                val content = it.content.replace("\"", "\"\"")
//                writer?.write(date + "\"${content}\"")
//                writer?.newLine()
//            }
//            writer?.flush()
//            outputStream?.close()
//        }
//    }
//
//    fun restoreFromCSV(context: Context, uri: Uri) {
//        val contentResolver: ContentResolver = context.contentResolver
//        val inputStream = contentResolver.openInputStream(uri)
//        val reader = inputStream?.bufferedReader()
//        var line: String = reader?.readLine().toString()
//        if (line != """"Date","Content"""") {
//            Toast.makeText(context, "File không hợp lệ", Toast.LENGTH_SHORT).show()
//        } else {
//            var date = ""
//            var content = ""
//            var count = 0
//            while (reader?.readLine().also { if (it != null) { line = it } } != null) {
//                if (count == 0) {
//                    val columns = line.split(",", limit = 2)
//                    date = columns[0]
//                    content = columns[1]
//                } else {
//                    content += "\n" + line
//                }
//                count += line.count { it == '"' }
//                if ((count % 2 == 0 && line.last() == '"')) {
//                    content = content.substring(1, content.length - 1)
//                    content = content.replace("\"\"", "\"")
//                    addDiary(Data(date.toLong(), content))
//                    date = ""
//                    content = ""
//                    count = 0
//                }
//            }
//            inputStream?.close()
//        }
//    }

    fun getDiary(date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentNote.emit(dao.getNote(date))
        }
    }

    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.insertNote(note)
    }

    fun updateDiary(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.updateNote(note)
    }

    fun deleteDiary(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        dao.deleteNote(note)
    }
}

class NoteViewModelFactory(private val dao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}