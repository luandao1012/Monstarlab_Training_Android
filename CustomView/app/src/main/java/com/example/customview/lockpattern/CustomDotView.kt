package com.example.customview.lockpattern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.customview.R

class CustomDotView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    companion object {
        const val RADIUS = 20
    }

    private val paint by lazy { Paint() }
    var key: String? = null
        private set

    init {
        setWillNotDraw(false)
    }

    fun setKey(key: String) {
        this.key = key
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(width / 2f, height / 2f, RADIUS.toFloat(), paint)
    }

    fun setDotViewColor(color: Int) {
        paint.color = color
        invalidate()
    }
}