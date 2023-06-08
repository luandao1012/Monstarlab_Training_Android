package com.example.musicapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Song
import com.example.musicapplication.repository.FavouriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel : ViewModel() {
    private val favouriteRepository by lazy { FavouriteRepository() }
    var mp3FavouriteList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
        private set

    fun getAllMp3Favourite() {
        viewModelScope.launch(Dispatchers.IO) {
            mp3FavouriteList.emit(favouriteRepository.getAllMp3Favourite() as ArrayList<Song>)
        }
    }
}