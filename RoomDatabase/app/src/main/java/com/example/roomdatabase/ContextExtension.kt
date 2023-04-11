package com.example.roomdatabase

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.roomdatabase.data.Diary
import com.example.roomdatabase.databinding.DatePickerDialogBinding
import java.util.*

fun Context.showDatePickerDialog(
    date: Long,
    callback: ((date: Long) -> Unit)? = null
) {
    val binding = DatePickerDialogBinding.inflate(LayoutInflater.from(this))
    val builder = AlertDialog.Builder(this)
    builder.setView(binding.root)
    val calendar = Calendar.getInstance(Locale("vi", "VN"))
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

fun Context.showDialog(
    diary: Diary?,
    title: String,
    message: String,
    callback: ((diary: Diary?) -> Unit)? = null
) {
    val builder = AlertDialog.Builder(this)
    builder.apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("Có") { _, _ ->
            callback?.invoke(diary)
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