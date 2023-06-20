package com.example.customview.lockpattern

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed

class CustomLockPatternView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    LinearLayout(context, attrs) {
    companion object {
        private const val MIN_DOT = 4
        private const val MAX_DOT = 9
        private const val DOT_COLOR = Color.DKGRAY
        private const val LINE_COLOR = Color.LTGRAY
        private const val ERROR_COLOR = Color.RED
        private const val SUCCESS_COLOR = Color.GREEN
        private const val PADDING_DOT = 30
    }

    private var touchedPointX = 0f
    private var touchedPointY = 0f
    private val dotKeyList = arrayOf(
        arrayOf("1", "2", "3"),
        arrayOf("4", "5", "6"),
        arrayOf("7", "8", "9")
    )
    private var dotList = arrayListOf<Dot>()
    private var selectedDotList = arrayListOf<Dot>()
    private var state = PatternState.INITIAL
    private var password = ""
    private var setPasswordCallback: ((password: String) -> Unit)? = null
    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = 12f
            color = Color.DKGRAY
        }
    }

    init {
        orientation = VERTICAL
        drawPatternView()
        setWillNotDraw(false)
    }

    fun setPasswordListener(callback: ((password: String) -> Unit)? = null) {
        setPasswordCallback = callback
    }

    private fun drawPatternView(
        rowSize: Int = 3,
        columnSize: Int = 3,
        layoutParams: ViewGroup.LayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { weight = 1f },
        dotKeys: Array<Array<String>> = dotKeyList
    ) {
        for (rowIndex in 0 until rowSize) {
            createRow(this@CustomLockPatternView, layoutParams).apply {
                for (columnIndex in 0 until columnSize) {
                    createRow(this, LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                    }).apply {
                        createColumn(this, dotKeys[rowIndex][columnIndex])
                    }
                }
            }
        }
    }

    private fun createRow(view: LinearLayout, layoutParams: ViewGroup.LayoutParams): LinearLayout {
        view.addView(LinearLayout(context).apply {
            this.layoutParams = layoutParams
            this.gravity = Gravity.CENTER
        })

        return view.getChildAt(view.childCount - 1) as LinearLayout
    }

    private fun createColumn(view: LinearLayout, keys: String) {
        view.addView(
            CustomDotView(context).apply {
                setDotViewColor(DOT_COLOR)
                setKey(keys)
            }
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        addInitialData()
        drawLine(canvas)

        if (state == PatternState.ERROR) {
            object : CountDownTimer(1000, 2000) {
                override fun onTick(millisUntilFinished: Long) = Unit

                override fun onFinish() {
                    reset()
                }
            }.start()
        }
    }

    private fun addInitialData() {
        if (dotList.size != 0) return

        forEachIndexed { rowIndex, view ->
            (view as? ViewGroup)?.forEachIndexed { columnIndex, viewGroup ->
                (viewGroup as? ViewGroup)?.forEach { dotView ->
                    if (dotView !is CustomDotView) return
                    val left = dotView.width / 2 * (columnIndex * 2 + 1) - CustomDotView.RADIUS - PADDING_DOT
                    val top = dotView.height / 2 * (rowIndex * 2 + 1) + CustomDotView.RADIUS + PADDING_DOT
                    val right = dotView.width / 2 * (columnIndex * 2 + 1) + CustomDotView.RADIUS + PADDING_DOT
                    val bottom = dotView.height / 2 * (rowIndex * 2 + 1) - CustomDotView.RADIUS - PADDING_DOT
                    dotList.add(
                        Dot(rowIndex, columnIndex, left, top, right, bottom, dotView.key)
                    )
                }
            }
        }
    }

    private fun drawLine(canvas: Canvas?) {
        selectedDotList.forEachIndexed { index, _ ->
            if (index + 1 < selectedDotList.size) {
                canvas?.drawLine(
                    (selectedDotList[index].rightPoint + selectedDotList[index].leftPoint) / 2.toFloat(),
                    (selectedDotList[index].bottomPoint + selectedDotList[index].topPoint) / 2.toFloat(),
                    (selectedDotList[index + 1].rightPoint + selectedDotList[index + 1].leftPoint) / 2.toFloat(),
                    (selectedDotList[index + 1].bottomPoint + selectedDotList[index + 1].topPoint.toFloat()) / 2.toFloat(),
                    paint
                )
            }
        }

        if (state == PatternState.START) {
            canvas?.drawLine(
                (selectedDotList.last().rightPoint + selectedDotList.last().leftPoint) / 2.toFloat(),
                (selectedDotList.last().bottomPoint + selectedDotList.last().topPoint.toFloat()) / 2.toFloat(),
                touchedPointX, touchedPointY, paint
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            touchedPointX = event.x
            touchedPointY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (state == PatternState.ERROR) {
                        return false
                    }
                    reset()
                    if (isTouchedDot(touchedPointX, touchedPointY)) {
                        updatePatternState(PatternState.START)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (selectedDotList.size != 0 && selectedDotList.size >= MIN_DOT) {
                        setPassword()
                    } else if (selectedDotList.size != 0) {
                        updatePatternState(PatternState.ERROR)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (state == PatternState.START && selectedDotList.size < MAX_DOT) {
                        isTouchedDot(touchedPointX, touchedPointY)
                        invalidate()
                    }
                }
            }
            return true
        }
        return false
    }

    private fun isTouchedDot(pointX: Float, pointY: Float): Boolean {
        val touchedDot = getDotByPoint(pointX, pointY) ?: return false
        if (isDotSelected(touchedDot)) return false
        selectedDotList.takeIf { it.size != 0 }?.last()?.let { lastSelectedDot ->
            val rowIndex = lastSelectedDot.rowIndex + touchedDot.rowIndex
            val columnIndex = lastSelectedDot.columnIndex + touchedDot.columnIndex
            if (rowIndex % 2 == 0 && columnIndex % 2 == 0) {
                getDotByIndex(rowIndex / 2, columnIndex / 2)?.let {
                    if (!isDotSelected(it)) {
                        selectedDotList.add(it)
                    }
                }
            }
        }
        if (selectedDotList.size < MAX_DOT) {
            selectedDotList.add(touchedDot)
        }
        return true
    }

    private fun getDotByPoint(pointX: Float, pointY: Float) = dotList.firstOrNull {
        (it.leftPoint <= pointX) and (it.rightPoint >= pointX) and (it.topPoint >= pointY) and (it.bottomPoint <= pointY)
    }

    private fun isDotSelected(dot: Dot) = selectedDotList.firstOrNull { it == dot } != null

    private fun getDotByIndex(rowIndex: Int, columnIndex: Int) = dotList.firstOrNull {
        it.rowIndex == rowIndex && it.columnIndex == columnIndex
    }

    private fun setPassword() {
        selectedDotList.forEach {
            password += it.key
        }
        setPasswordCallback?.invoke(password)
    }

    private fun reset() {
        updatePatternState(PatternState.INITIAL)
        selectedDotList.clear()
        password = ""
        invalidate()
    }

    fun updatePatternState(state: PatternState) {
        this.state = state
        val dotColor: Int
        val lineColor: Int
        when (state) {
            PatternState.SUCCESS -> {
                dotColor = SUCCESS_COLOR
                lineColor = SUCCESS_COLOR
            }

            PatternState.ERROR -> {
                dotColor = ERROR_COLOR
                lineColor = ERROR_COLOR
            }

            else -> {
                dotColor = DOT_COLOR
                lineColor = LINE_COLOR
            }
        }
        paint.color = lineColor
        selectedDotList.forEach { dot ->
            (((this.getChildAt(dot.rowIndex) as? ViewGroup)
                ?.getChildAt(dot.columnIndex) as? ViewGroup)
                ?.getChildAt(0) as? CustomDotView)
                ?.setDotViewColor(dotColor)
        }
        invalidate()
    }
}