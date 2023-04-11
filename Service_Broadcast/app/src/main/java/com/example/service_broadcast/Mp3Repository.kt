package com.example.service_broadcast

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class Mp3Repository {
    fun getAllMp3(context: Context): List<Song> {
        var listMp3 = arrayListOf<Song>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(0)
                val singer = cursor.getString(1)
                val index = cursor.getString(2).toLong()
                val songUri = ContentUris.withAppendedId(uri, index)
                listMp3.add(Song(name, singer, songUri))
            }
        }
        cursor?.close()
        return listMp3
    }
}