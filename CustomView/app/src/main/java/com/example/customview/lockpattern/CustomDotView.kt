package com.example.customview.lockpattern

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.widget.AppCompatImageView
import com.example.customview.R

class CustomDotView constructor(context: Context) : AppCompatImageView(context) {
    var key: String? = null
        private set

    init {
        setImageResource(R.drawable.dot_lock_pattern)
        setWillNotDraw(false)
    }

    fun setKey(key: String) {
        this.key = key
    }

    fun setDotViewColor(color: Int) {
        (drawable as? GradientDrawable)?.setColor(color)
    }
}