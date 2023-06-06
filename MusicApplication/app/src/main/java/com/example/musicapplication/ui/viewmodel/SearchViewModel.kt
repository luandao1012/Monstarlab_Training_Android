package com.example.musicapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.ItemSearch
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _mp3SearchList = MutableStateFlow<ArrayList<ItemSearch>>(arrayListOf())
    var mp3SearchList: StateFlow<ArrayList<ItemSearch>> = _mp3SearchList
    private val _mp3RecommendList = MutableStateFlow<ArrayList<Song>>(arrayListOf())
    var mp3RecommendList: StateFlow<ArrayList<Song>> = _mp3RecommendList

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiBuilder.mp3ApiSearchService.searchMp3(query)
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    if (result == true) {
                        val listItem = response.body()?.data?.get(0)
                        listItem?.listItem?.let { _mp3SearchList.emit(it) }
                    }
                }
            } catch (e: Exception) {
                Log.d("test123", e.message.toString())
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
}