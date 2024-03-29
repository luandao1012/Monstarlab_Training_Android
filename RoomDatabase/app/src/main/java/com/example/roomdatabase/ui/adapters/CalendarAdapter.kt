package com.example.roomdatabase.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.roomdatabase.R
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.databinding.ItemCalendarBinding
import com.example.roomdatabase.setTimeCalendar
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class CalendarAdapter : Adapter<CalendarAdapter.CalendarViewHolder>() {
    private var dateCalendarList = listOf<Note>()
    private var callbackSaveDateSelected: ((date: Long) -> Unit)? = null
    private var callbackDoubleClick: ((date: Note) -> Unit)? = null
    private var currentMonth = -1
    private var dateSelected = 0L
    private val calendarToday = Calendar.getInstance()

    fun setData(list: List<Note>) {
        dateCalendarList = list
        notifyDataSetChanged()
    }

    fun setCurrentMonth(month: Int) {
        currentMonth = month
    }

    fun setDateSelectedCallback(callback: (date: Long) -> Unit) {
        callbackSaveDateSelected = callback
    }

    fun setDateSelected(date: Long) {
        dateSelected = date
    }

    fun setDoubleClickListener(callback: (date: Note) -> Unit) {
        callbackDoubleClick = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        calendarToday.setTimeCalendar()
        val binding =
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val calendarViewHolder = CalendarViewHolder(binding)
        calendarViewHolder.bindListeners()
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
                date.text = calendar.get(Calendar.DATE).toString()
                if (calendar.get(Calendar.MONTH) == currentMonth) {
                    if (position % 7 == 0) {
                        date.setTextColor(Color.RED)
                    } else {
                        date.setTextColor(Color.BLACK)
                    }
                } else {
                    if (position % 7 == 0) {
                        date.setTextColor(Color.parseColor("#75FF0000"))
                    } else {
                        date.setTextColor(Color.GRAY)
                    }
                }
                if (day.date == calendarToday.timeInMillis) {
                    date.setTextColor(Color.GREEN)
                }
                if (day.date == dateSelected) {
                    date.setBackgroundResource(R.drawable.bg_date_selected)
                } else {
                    if (day.content.isNotEmpty()) {
                        date.setBackgroundResource(R.drawable.bg_diary)
                    } else {
                        date.setBackgroundColor(Color.WHITE)
                    }
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bindListeners() {
            var firstClickTime = 0L
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
                            if (clickDuration < 150) {
                                onDoubleClick()
                            }
                            changeDateColor()
                            clickHandler.removeCallbacksAndMessages(null)
                        }, 150)
                    }
                }
                true
            }
        }

        private fun onDoubleClick() {
            if (adapterPosition != -1) callbackDoubleClick?.invoke(dateCalendarList[adapterPosition])
        }

        private fun changeDateColor() {
            val index = dateCalendarList.indexOfFirst { it.date == dateSelected }
            val date =
                if (adapterPosition != -1) dateCalendarList[adapterPosition] else null
            if (date != null) {
                if (index != -1) notifyItemChanged(index)
                callbackSaveDateSelected?.invoke(date.date)
                notifyItemChanged(adapterPosition)
            }
        }
    }
}