package com.example.musicapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.ItemSearch
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    var mp3SearchList = MutableStateFlow<ArrayList<ItemSearch>>(arrayListOf())
        private set
    var mp3RecommendList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
        private set

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiBuilder.mp3ApiSearchService.searchMp3(query)
            if (response.isSuccessful) {
                val result = response.body()?.result
                if (result == true) {
                    val listItem = response.body()?.data?.get(0)
                    listItem?.listItem?.let { mp3SearchList.emit(it) }
                }
            }
        }
    }

    fun getMp3Recommend(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiBuilder.mp3ApiService.getMp3Recommend(id)
            if (response.isSuccessful) {
                response.body()?.data?.mp3Recommend?.let { mp3RecommendList.emit(it) }
            }
        }
    }
}