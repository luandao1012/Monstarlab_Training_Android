package com.example.musicapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val store = Firebase.firestore
    private val _mp3ChartsList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    var mp3ChartsList: StateFlow<ArrayList<Song>> = _mp3ChartsList

    fun getMp3Charts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data =
                    store.collection("ListMp3Favourite")
                        .document("List")
                        .collection("List")
                        .get()
                        .await()
                val listIdMp3Favourite = data.documents.map { it.id }
                val response = ApiBuilder.mp3ApiService.getMp3Charts()
                if (response.isSuccessful) {
                    val listSong = response.body()?.data?.mp3Charts
                    listSong?.forEach { song ->
                        if (listIdMp3Favourite.contains(song.id)) {
                            song.isFavourite = true
                        }
                    }
                    listSong?.let { _mp3ChartsList.emit(it) }
                }
            } catch (e: Exception) {
                Log.d("test123", e.toString())
            }
        }
    }

    fun removeFavourite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStateFlow = _mp3ChartsList.value.map { item ->
                if (item.id == id) {
                    item.copy(isFavourite = false)
                } else {
                    item
                }
            }
            _mp3ChartsList.emit(newStateFlow as ArrayList<Song>)
        }
    }
}