package com.example.musicapplication

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.security.Provider.Service
import java.text.SimpleDateFormat

private val timeFormat by lazy { SimpleDateFormat("mm:ss") }
fun ImageView.loadImage(link: String) {
    Glide.with(this.context).load(link).into(this)
}

fun Int.formatSongTime(): String {
    return timeFormat.format(this * 1000)
}

fun <T> AppCompatActivity.collectFlow(flow: Flow<T>, callback: (T) -> Unit) {
    lifecycleScope.launch {
        flow.collect(callback)
    }
}

fun <T> Fragment.collectFlow(flow: Flow<T>, callback: (T) -> Unit) {
    lifecycleScope.launch {
        flow.collect(callback)
    }
}

