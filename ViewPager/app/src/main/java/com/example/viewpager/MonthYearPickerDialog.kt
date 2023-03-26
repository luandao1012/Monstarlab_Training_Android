package com.example.viewpager

import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.viewpager.databinding.DatePickerDialogBinding


class MonthYearPickerDialog : DialogFragment() {
    companion object {
        const val MIN_YEAR = 2000
        const val MAX_YEAR = 2099
    }

    private val binding by lazy { DatePickerDialogBinding.inflate(layoutInflater) }
    private var callbackDatePicker: OnDateSetListener? = null

    fun setDatePickerListener(callback: OnDateSetListener?) {
        this.callbackDatePicker = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder: AlertDialog.Builder? = null
        (activity as? MainActivity)?.let {
            builder = AlertDialog.Builder(it)
            binding.pickerMonth.apply {
                minValue = 1
                maxValue = 12
                value = it.currentMonthShowing + 1
            }
            binding.pickerYear.apply {
                minValue = MIN_YEAR
                maxValue = MAX_YEAR
                value = it.currentYearShowing
            }
            builder?.setView(binding.root)?.setPositiveButton("OK") { _, _ ->
                callbackDatePicker?.onDateSet(
                    null,
                    binding.pickerYear.value,
                    binding.pickerMonth.value,
                    0
                )
            }?.setNegativeButton(
                "CANCEL"
            ) { _, _ -> this.dialog!!.cancel() }
        }
        return builder!!.create()
    }
}