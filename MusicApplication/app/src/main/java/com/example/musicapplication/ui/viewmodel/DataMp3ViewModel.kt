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
import com.example.musicapplication.model.ItemSearch
import com.example.musicapplication.model.Song
import com.example.musicapplication.model.Source
import com.example.musicapplication.network.ApiBuilder
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DataMp3ViewModel : ViewModel() {
    companion object {
        const val MESSAGE_SUCCESS = "Success"
    }

    private val store = Firebase.firestore
    private val _mp3ChartsList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    private val _mp3SearchList = MutableStateFlow<ArrayList<ItemSearch>>(arrayListOf())
    private val _mp3RecommendList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    private val _mp3FavouriteList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    private val _mp3OfflineList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    var mp3ChartsList: StateFlow<ArrayList<Song>> = _mp3ChartsList
    var mp3SearchList: StateFlow<ArrayList<ItemSearch>> = _mp3SearchList
    var mp3RecommendList: StateFlow<ArrayList<Song>> = _mp3RecommendList
    var mp3FavouriteList: StateFlow<ArrayList<Song>> = _mp3FavouriteList
    var mp3OfflineList: StateFlow<ArrayList<Song>> = _mp3OfflineList

    fun getMp3Charts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data =
                    store.collection("ListMp3Favourite")
                        .document("List")
                        .collection("List")
                        .get()
                        .await()
                val codeMp3Favourite = mutableListOf<String>()
                data.documents.forEach {
                    codeMp3Favourite += it.id
                }
                val response = ApiBuilder.mp3ApiService.getMp3Charts()
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    if (message == MESSAGE_SUCCESS) {
                        val listSong = response.body()?.data?.mp3Charts
                        listSong?.forEach { song ->
                            if (codeMp3Favourite.contains(song.id)) {
                                song.isFavourite = true
                            }
                        }
                        listSong?.let { _mp3ChartsList.emit(it) }
                    }
                }
            } catch (e: Exception) {
                Log.d("test123", e.toString())
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiBuilder.mp3ApiSearchService.searchMp3(query)
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    if (result == true) {
                        withContext(Dispatchers.Main) {
                            val listItem = response.body()?.data?.get(0)
                            listItem?.listItem?.let { _mp3SearchList.emit(it) }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("test123", e.toString())
            }
        }
    }

    fun getAllMp3Favourite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data =
                    store.collection("ListMp3Favourite")
                        .document("List")
                        .collection("List")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .await()
                val codeMp3Favourite = data?.documents
                val listMp3 = arrayListOf<Song>()
                codeMp3Favourite?.forEach { documents ->
                    val song = documents.data?.get("song").toString()
                    listMp3.add(Gson().fromJson(song, Song::class.java))
                }
                _mp3FavouriteList.emit(listMp3)
            } catch (e: Exception) {
                Log.e("test123", e.message.toString())
            }
        }
    }

    fun getMp3Recommend(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiBuilder.mp3ApiService.getMp3Recommend(id)
                if (response.isSuccessful) {
                    response.body()?.data?.mp3Recommend?.let { _mp3RecommendList.emit(it) }
                }
            } catch (e: Exception) {
                Log.e("test123", e.message.toString())
            }
        }
    }

    fun getOfflineMp3(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                    _mp3OfflineList.emit(listMp3)
                }
            } catch (e: Exception) {
                Log.d("test123", e.message.toString())
            }
        }
    }
}