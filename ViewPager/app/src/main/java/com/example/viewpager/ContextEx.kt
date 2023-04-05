package com.example.viewpager

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.viewpager.databinding.DatePickerDialogBinding

fun Context.showMonthYearPicker(
    month: Int,
    year: Int,
    callback: ((month: Int, year: Int) -> Unit)? = null
) {
    val layoutInflater = LayoutInflater.from(this)
    val binding = DatePickerDialogBinding.inflate(layoutInflater)
    val builder = AlertDialog.Builder(this)
    builder.setView(binding.root)
    binding.pickerMonth.apply {
        minValue = 1
        maxValue = 12
        value = month
    }
    binding.pickerYear.apply {
        minValue = 2000
        maxValue = 2099
        value = year
    }
    builder.setPositiveButton("OK") { _, _ ->
        callback?.invoke(binding.pickerMonth.value, binding.pickerYear.value)
    }
    builder.setNegativeButton("CANCEL") { _, _ -> }
    val dialog = builder.create()
    dialog.show()
}
