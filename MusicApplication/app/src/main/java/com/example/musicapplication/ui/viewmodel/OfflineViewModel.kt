package com.example.musicapplication.ui.viewmodel

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Genre
import com.example.musicapplication.model.Song
import com.example.musicapplication.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

class OfflineViewModel : ViewModel() {
    var mp3OfflineList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
        private set

    fun getOfflineMp3(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listMp3 = arrayListOf<Song>()
            val contentResolver: ContentResolver = context.contentResolver
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.GENRE
            )
            val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val name = cursor.getString(0)
                    val singer = cursor.getString(1)
                    val index = cursor.getString(2).toLong()
                    val songUri = ContentUris.withAppendedId(uri, index)
                    val path = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val albumId = cursor.getLong(5)
                    val genre = cursor.getString(6) ?: ""
                    val image = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )
                    if (path.endsWith(".mp3") && File(path).exists()) {
                        listMp3.add(
                            Song(
                                id = index.toString(),
                                name = name,
                                singer = singer,
                                image = image.toString(),
                                duration = (duration / 1000).toInt(),
                                source = Source(songUri.toString()),
                                genre = Genre(genre)
                            )
                        )
                    }
                }
                cursor.close()
                mp3OfflineList.emit(listMp3)
            }
        }
    }
}
