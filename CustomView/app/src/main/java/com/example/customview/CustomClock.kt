package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.os.postDelayed
import kotlinx.coroutines.Runnable
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin


class CustomClock @JvmOverloads constructor(context: Context, att: AttributeSet? = null) :
    View(context, att) {
    private val paint by lazy { Paint() }
    private val hours = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private var radius: Int = 0
    private val padding = 50
    private val calendar by lazy { Calendar.getInstance() }
    private var onClickListener: ((time: Long) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private val rect = Rect()
    private val runnable = object : Runnable {
        override fun run() {
            invalidate()
            calendar.timeInMillis += 1000
            handler.postDelayed(this, 1000)
        }
    }

    fun onClickListener(callback: ((time: Long) -> Unit)? = null) {
        onClickListener = callback
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.post(runnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(runnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawHour(canvas)
        drawClockHands(canvas)
    }

    private fun drawClockHands(canvas: Canvas) {
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        drawHandLine(canvas, (hour + minute / 60f) * 5, Calendar.HOUR)
        drawHandLine(canvas, minute.toFloat(), Calendar.MINUTE)
        drawHandLine(canvas, second.toFloat(), Calendar.SECOND)
    }

    private fun drawHandLine(canvas: Canvas, time: Float, type: Int) {
        val angle = Math.PI / 30 * (time - 15)
        val handRadius = if (type == Calendar.HOUR) radius * 0.6 else radius * 0.75
        if (type == Calendar.SECOND) {
            paint.color = Color.RED
        }
        paint.strokeWidth = 10f
        canvas.drawLine(
            width / 2f,
            width / 2f,
            (width / 2 + (handRadius) * cos(angle)).toFloat(),
            (width / 2 + (handRadius) * sin(angle)).toFloat(),
            paint
        )
    }

    private fun drawHour(canvas: Canvas) {
        val textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30f, resources.displayMetrics)
                .toInt()
        paint.textSize = textSize.toFloat()
        paint.style = Paint.Style.FILL
        var rotate = 30
        for (i in hours) {
            canvas.save()
            paint.getTextBounds(i.toString(), 0, i.toString().length, rect)
            val x =
                width / 2 + radius * 0.75 * cos(Math.PI / 6 * (i - 3)) - rect.width() / 2
            val y =
                width / 2 + radius * 0.75 * sin(Math.PI / 6 * (i - 3)) + rect.height() / 2
            canvas.rotate(
                rotate.toFloat(),
                (x + rect.width() / 2).toFloat(),
                (y - rect.height() / 2).toFloat()
            )
            canvas.drawText(i.toString(), x.toFloat(), y.toFloat(), paint)
            rotate += 30
            canvas.restore()
        }
    }

    private fun drawBackground(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = Color.BLACK
        canvas.drawCircle(
            width / 2f,
            width / 2f,
            (radius - padding).toFloat(),
            paint
        )
        paint.style = Paint.Style.FILL
        canvas.drawCircle(width / 2f, width / 2f, 12f, paint)
        for (i in 0 until 60) {
            val angle = Math.PI / 30 * (i - 15)
            var length = if (i % 5 == 0) 25 else 10
            val startX = width / 2f + (radius - padding - length) * cos(angle).toFloat()
            val startY = width / 2f + (radius - padding - length) * sin(angle).toFloat()
            val stopX = width / 2f + (radius - padding) * cos(angle).toFloat()
            val stopY = width / 2f + (radius - padding) * sin(angle).toFloat()
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        radius = measuredWidth.coerceAtMost(measuredHeight) / 2
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onClickListener?.invoke(calendar.timeInMillis)
                true
            }

            else -> false
        }
    }

    fun setTime(time: Long) {
        calendar.timeInMillis = time
        invalidate()
    }
}