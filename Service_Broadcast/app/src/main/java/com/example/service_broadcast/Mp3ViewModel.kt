package com.example.service_broadcast

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File


class Mp3ViewModel : ViewModel() {
    var allMp3 = MutableLiveData<ArrayList<Song>>()

    fun getAllMp3Files(context: Context) {
        val listMp3 = arrayListOf<Song>()
        val contentResolver: ContentResolver = context.contentResolver
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA
        )
        val cursor = contentResolver.query(uri, projection, selection, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(0)
                val singer = cursor.getString(1)
                val index = cursor.getString(2).toLong()
                val songUri = ContentUris.withAppendedId(uri, index)
                val path = cursor.getString(3)
                if (path.endsWith(".mp3") && File(path).exists()) {
                    listMp3.add(Song(name, singer, songUri))
                }
            }
            cursor.close()
            allMp3.value = listMp3
        }
    }
}
