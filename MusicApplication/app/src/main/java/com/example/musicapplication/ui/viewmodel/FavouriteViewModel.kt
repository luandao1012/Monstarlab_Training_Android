package com.example.musicapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Song
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavouriteViewModel : ViewModel() {
    private val store = Firebase.firestore
    private val _mp3FavouriteList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    var mp3FavouriteList: StateFlow<ArrayList<Song>> = _mp3FavouriteList

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
                val listMp3 = arrayListOf<Song>()
                data?.documents?.forEach { documents ->
                    val song = documents.data?.get("song").toString()
                    listMp3.add(Gson().fromJson(song, Song::class.java))
                }
                _mp3FavouriteList.emit(listMp3)
            } catch (e: Exception) {
                Log.e("test123", e.message.toString())
            }
        }
    }
}