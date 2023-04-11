package com.example.service_broadcast

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class Mp3ViewModel : ViewModel() {
    var allMp3 = MutableLiveData<List<Song>>()
    private val mp3Repository by lazy { Mp3Repository() }

    fun getAllMp3Files(context: Context) {
        allMp3.value = mp3Repository.getAllMp3(context)
    }
}