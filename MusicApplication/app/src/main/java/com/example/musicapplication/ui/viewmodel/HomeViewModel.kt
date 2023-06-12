package com.example.musicapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.example.musicapplication.repository.FavouriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val favouriteRepository by lazy { FavouriteRepository() }

    var mp3ChartsList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
        private set

    fun getMp3Charts() {
        viewModelScope.launch(Dispatchers.IO) {
            val listIdMp3Favourite = favouriteRepository.getAllIdMp3Favourite()
            val response = ApiBuilder.mp3ApiService.getMp3Charts()
            if (response.isSuccessful) {
                val listSong = response.body()?.data?.mp3Charts
                listSong?.forEach { song ->
                    if (listIdMp3Favourite.contains(song.id)) {
                        song.isFavourite = true
                    }
                }
                listSong?.let { mp3ChartsList.emit(it) }
            }
        }
    }

    fun removeFavourite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStateFlow = mp3ChartsList.value.map { item ->
                if (item.id == id) {
                    item.copy(isFavourite = false)
                } else {
                    item
                }
            }
            mp3ChartsList.emit(newStateFlow as ArrayList<Song>)
        }
    }
}