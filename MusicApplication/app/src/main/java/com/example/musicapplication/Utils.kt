package com.example.musicapplication

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat

private val timeFormat by lazy { SimpleDateFormat("mm:ss") }
fun ImageView.loadImage(link: String) {
    Glide.with(this.context).load(link).into(this)
}

fun Int.formatSongTime(): String {
    return timeFormat.format(this * 1000)
}
