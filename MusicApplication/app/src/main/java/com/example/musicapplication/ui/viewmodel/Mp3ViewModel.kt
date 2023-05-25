package com.example.musicapplication.ui.viewmodel

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Genre
import com.example.musicapplication.model.ItemSearch
import com.example.musicapplication.model.Song
import com.example.musicapplication.model.Source
import com.example.musicapplication.network.ApiBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.checkerframework.checker.units.qual.A
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Mp3ViewModel : ViewModel() {
    companion object {
        const val MESSAGE_SUCCESS = "Success"
    }

    private val store = Firebase.firestore
    var mp3ChartsList = MutableLiveData<ArrayList<Song>?>()
    var mp3Genres = MutableLiveData<ArrayList<Genre>?>()
    var mp3Search = MutableLiveData<ArrayList<ItemSearch>?>()
    var mp3FavouriteList = MutableLiveData<ArrayList<Song>?>()
    var mp3OfflineList = MutableLiveData<ArrayList<Song>>()
    var mp3Recommend = MutableLiveData<ArrayList<Song>>()
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
                        mp3ChartsList.postValue(listSong)
                    }
                }
            } catch (e: Exception) {
                Log.d("test123", e.toString())
            }
        }
    }

    fun getGenres(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiBuilder.mp3ApiService.getGenres(id)
                if (response.isSuccessful) {
                    if (response.body()?.message == MESSAGE_SUCCESS) {
                        withContext(Dispatchers.Main) {
                            mp3Genres.value = response.body()?.data?.mp3Genres
                        }
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
                            mp3Search.value = listItem?.listItem
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("test123", e.toString())
            }
        }
    }

    fun addFavourite(id: String, code: String, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isFavourite) {
                    val favourite = hashMapOf(
                        "code" to code,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    store.collection("ListMp3Favourite").document("List").collection("List")
                        .document(id).set(favourite).await()
                } else {
                    store.collection("ListMp3Favourite").document("List").collection("List")
                        .document(id).delete().await()
                }
            } catch (e: Exception) {
                Log.e("test123", e.message.toString())
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
                val codeMp3Favourite = data.documents
                val listMp3 = arrayListOf<Song>()
                codeMp3Favourite.forEach { documents ->
                    val response =
                        ApiBuilder.mp3ApiService.getMp3Info(documents.data?.get("code") as String)
                    if (response.isSuccessful) {
                        response.body()?.data?.let { song ->
                            song.isFavourite = true
                            listMp3.add(song)
                        }
                    }
                }
                mp3FavouriteList.postValue(listMp3)
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
                    mp3Recommend.postValue(response.body()?.data?.mp3Recommend)
                }
            } catch (e: Exception) {
                Log.e("test123", e.message.toString())
            }
        }
    }

    fun downloadMp3(context: Context, id: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ApiBuilder.mp3ApiService.getLinkStreaming("http://api.mp3.zing.vn/api/streaming/audio/${id}/320")
                response.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val url = response.headers()[("Location")].toString()
                        val request = DownloadManager.Request(Uri.parse(url))
                            .setTitle(fileName)
                            .setDescription("Đang tải...")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                fileName
                            )
                        val downloadManager =
                            context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                        downloadManager?.enqueue(request)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) = Unit
                })
            } catch (e: Exception) {
                Log.d("test123", e.message.toString())
            }
        }
    }

    fun getOfflineMp3(context: Context) {
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
                    val genre = cursor.getString(6)
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
                mp3OfflineList.value = listMp3
            }
        } catch (e: Exception) {
            Log.d("test123", e.message.toString())
        }
    }
}