package com.example.roomdatabase

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.databinding.DatePickerDialogBinding
import com.example.roomdatabase.databinding.TimePickerDialogBinding
import java.util.*

fun Context.showDatePickerDialog(
    date: Long,
    callback: ((date: Long) -> Unit)? = null
) {
    val binding = DatePickerDialogBinding.inflate(LayoutInflater.from(this))
    val builder = AlertDialog.Builder(this)
    builder.setView(binding.root)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    binding.pickerMonth.apply {
        minValue = 1
        maxValue = 12
        value = calendar[Calendar.MONTH] + 1
    }
    binding.pickerYear.apply {
        minValue = 2000
        maxValue = 2050
        value = calendar[Calendar.YEAR]
    }
    binding.pickerDate.apply {
        minValue = 1
        maxValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        value = calendar[Calendar.DATE]
    }
    builder.setPositiveButton("Đồng ý") { _, _ ->
        calendar.apply {
            set(Calendar.YEAR, binding.pickerYear.value)
            set(Calendar.MONTH, binding.pickerMonth.value - 1)
            set(Calendar.DATE, binding.pickerDate.value)
            setTimeCalendar()
        }
        callback?.invoke(calendar.timeInMillis)
    }
    builder.setNegativeButton("Hủy") { _, _ -> }
    val dialog = builder.create()
    dialog.show()
}

fun Context.showTimePickerDialog(
    time: Long,
    callback: ((time: Long) -> Unit)? = null
) {
    val binding = TimePickerDialogBinding.inflate(LayoutInflater.from(this))
    val builder = AlertDialog.Builder(this)
    builder.setView(binding.root)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    binding.pickerHour.apply {
        minValue = 0
        maxValue = 23
        value = calendar[Calendar.HOUR_OF_DAY]
    }
    binding.pickerMinute.apply {
        minValue = 0
        maxValue = 59
        value = calendar[Calendar.MINUTE]
    }
    builder.setPositiveButton("Đồng ý") { _, _ ->
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, binding.pickerHour.value)
            set(Calendar.MINUTE, binding.pickerMinute.value)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        callback?.invoke(calendar.timeInMillis)
    }
    builder.setNegativeButton("Hủy") { _, _ -> }
    val dialog = builder.create()
    dialog.show()
}

fun Context.showDialog(
    note: Note?,
    title: String,
    message: String,
    callback: ((note: Note?) -> Unit)? = null
) {
    val builder = AlertDialog.Builder(this)
    builder.apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("Có") { _, _ ->
            callback?.invoke(note)
        }
        setNegativeButton("Hủy") { _, _ -> }
    }
    val dialog = builder.create()
    dialog.show()
}

fun Calendar.setTimeCalendar() {
    this.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}