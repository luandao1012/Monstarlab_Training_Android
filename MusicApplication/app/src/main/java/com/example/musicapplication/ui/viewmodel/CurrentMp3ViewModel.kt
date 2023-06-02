package com.example.musicapplication.ui.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.formatSongTime
import com.example.musicapplication.model.Genre
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentMp3ViewModel : ViewModel() {
    private val _mp3Info = MutableStateFlow<Song?>(null)
    private val _isPlaying = MutableStateFlow(false)
    private val _currentTime = MutableStateFlow(0)
    private val _mp3GenresList = MutableStateFlow<ArrayList<Genre>>(arrayListOf())
    val mp3Info: StateFlow<Song?> = _mp3Info
    val isPlaying: StateFlow<Boolean> = _isPlaying
    val currentTime: StateFlow<Int> = _currentTime
    var mp3GenresList: StateFlow<ArrayList<Genre>> = _mp3GenresList
    private var countDownTimer: CountDownTimer? = null

    fun setMp3CurrentInfo(song: Song) {
        viewModelScope.launch {
            _mp3Info.emit(song)
        }
    }

    fun setIsPlaying(isPlaying: Boolean) {
        viewModelScope.launch {
            _isPlaying.emit(isPlaying)
        }
    }

    fun countDownTimeMp3(timeTotal: Long) {
        _currentTime.value = 0
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeTotal, 1000) {
            override fun onTick(p0: Long) {
                if (_isPlaying.value) {
                    _currentTime.value += 1
                }
            }

            override fun onFinish() = Unit
        }
        countDownTimer?.start()
    }

    fun getGenres(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiBuilder.mp3ApiService.getGenres(id)
                if (response.isSuccessful) {
                    if (response.body()?.message == DataMp3ViewModel.MESSAGE_SUCCESS) {
                        withContext(Dispatchers.Main) {
                            response.body()?.data?.mp3Genres?.let { _mp3GenresList.emit(it) }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("test123", e.toString())
            }
        }
    }
}