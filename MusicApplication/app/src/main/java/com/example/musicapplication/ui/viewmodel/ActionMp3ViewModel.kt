package com.example.musicapplication.ui.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActionMp3ViewModel : ViewModel() {
    private val store = Firebase.firestore
    private val _mp3Streaming = MutableStateFlow<Pair<String, Int>?>(null)
    val mp3Streaming: StateFlow<Pair<String, Int>?> = _mp3Streaming

    fun getStreaming(id: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ApiBuilder.mp3ApiService.getStreaming("http://api.mp3.zing.vn/api/streaming/audio/${id}/320")
                _mp3Streaming.emit(Pair(response.headers()[("Location")].toString(), position))
            } catch (e: Exception) {
                Log.d("test123", e.message.toString())
            }
        }
    }

    fun addFavourite(song: Song, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isFavourite) {
                    val favourite = hashMapOf(
                        "song" to Gson().toJson(song),
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    song.id?.let { id ->
                        store.collection("ListMp3Favourite").document("List").collection("List")
                            .document(id).set(favourite).await()
                    }
                } else {
                    song.id?.let { id ->
                        store.collection("ListMp3Favourite").document("List").collection("List")
                            .document(id).delete().await()
                    }
                }
            } catch (e: Exception) {
                Log.e("test123", e.message.toString())
            }
        }
    }

    fun downloadMp3(context: Context, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = _mp3Streaming.value?.first.toString()
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

            } catch (e: Exception) {
                Log.d("test123", e.message.toString())
            }
        }
    }
}