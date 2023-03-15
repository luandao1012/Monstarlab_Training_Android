package com.example.viewpager

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.viewpager.databinding.ItemCalendarBinding
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class CalendarAdapter : Adapter<CalendarAdapter.CalendarViewHolder>() {
    companion object {
        const val DAY_OF_WEEK = "Day of week"
        const val DATE_IN_MONTH = "Day in month"
        const val DATE_NOT_IN_MONTH = "Day not in month"
        var dateSelected = -1L
    }

    private var dateCalendarList = arrayListOf<DateCalendar>()
    private var isDoubleClick = false

    fun setData(list: ArrayList<DateCalendar>) {
        dateCalendarList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val calendarViewHolder = CalendarViewHolder(binding)
        calendarViewHolder.bindOnClick()
        return calendarViewHolder
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = dateCalendarList.size

    inner class CalendarViewHolder(private val binding: ItemCalendarBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            val day = dateCalendarList[position]
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = day.date
            binding.tvDay.let { date ->
                date.text =
                    if (day.type == DAY_OF_WEEK) {
                        day.day
                    } else {
                        calendar.get(Calendar.DATE).toString()
                    }
                if (day.type == DATE_NOT_IN_MONTH) {
                    date.setTextColor(Color.GRAY)
                } else {
                    date.setTextColor(Color.BLACK)
                }
                if (dateSelected == dateCalendarList[position].date && day.type != DAY_OF_WEEK) {
                    if (!isDoubleClick) {
                        date.setBackgroundColor(Color.CYAN)
                    } else {
                        val random = kotlin.random.Random.Default
                        date.setBackgroundColor(
                            Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
                        )
                    }
                } else {
                    date.setBackgroundColor(Color.WHITE)
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bindOnClick() {
            var firstClickTime = 0L
            val doubleClickTime = ViewConfiguration.getDoubleTapTimeout().toLong()
            val clickHandler = Handler(Looper.getMainLooper())
            binding.root.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        firstClickTime = SystemClock.elapsedRealtime()
                    }
                    MotionEvent.ACTION_UP -> {
                        clickHandler.postDelayed({
                            val lastClickTime = SystemClock.elapsedRealtime()
                            val clickDuration = lastClickTime - firstClickTime
                            if (clickDuration < doubleClickTime) {
                                onDoubleClick()
                            } else {
                                onClick()
                            }
                            clickHandler.removeCallbacksAndMessages(null)
                        }, doubleClickTime)
                    }
                }
                true
            }
        }

        private fun onClick() {
            isDoubleClick = false
            changeDateColor()
        }

        private fun onDoubleClick() {
            isDoubleClick = true
            changeDateColor()
        }

        private fun changeDateColor() {
            val index =
                dateCalendarList.indices.find { dateCalendarList[it].date == dateSelected }
            val date =
                if (adapterPosition != -1) dateCalendarList[adapterPosition] else null
            if (date != null && date.type != DAY_OF_WEEK) {
                if (index != null) notifyItemChanged(index)
                dateSelected = dateCalendarList[adapterPosition].date
                notifyItemChanged(adapterPosition)
            }
        }
    }
}