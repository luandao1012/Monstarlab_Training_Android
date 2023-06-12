package com.example.musicapplication.ui.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapplication.model.Genre
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.example.musicapplication.repository.FavouriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {
    private val favouriteRepository by lazy { FavouriteRepository() }
    var currentTime = MutableStateFlow(0)
        private set
    var isPlaying = MutableStateFlow(false)
        private set
    var mp3GenresList = MutableStateFlow<ArrayList<Genre>>(arrayListOf())
        private set
    private var countDownTimer: CountDownTimer? = null
    fun setIsPlaying(playing: Boolean) {
        viewModelScope.launch {
            isPlaying.emit(playing)
        }
    }

    fun changeFavourite(song: Song, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            favouriteRepository.changeFavourite(song, isFavourite)
        }
    }

    fun downloadMp3(context: Context, id: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                ApiBuilder.mp3ApiService.getStreaming("http://api.mp3.zing.vn/api/streaming/audio/${id}/320")
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
    }

    fun countDownTimeMp3(timeTotal: Long) {
        currentTime.value = 0
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeTotal, 1000) {
            override fun onTick(p0: Long) {
                if (isPlaying.value) {
                    currentTime.value += 1
                }
            }

            override fun onFinish() = Unit
        }
        countDownTimer?.start()
    }

    fun getGenres(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiBuilder.mp3ApiService.getGenres(id)
            if (response.isSuccessful) {
                response.body()?.data?.mp3Genres?.let { mp3GenresList.emit(it) }
            }
        }
    }
}