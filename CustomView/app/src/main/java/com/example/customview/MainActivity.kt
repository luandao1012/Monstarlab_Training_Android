package com.example.customview

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.customview.databinding.ActivityMainBinding
import com.example.customview.databinding.TimePickerDialogBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {

    }

    private fun initListeners() {
        binding.clock.onClickListener {
            showDialog(it) { time ->
                binding.clock.setTime(time)
            }
        }
    }

    private fun showDialog(time: Long, callback: ((time: Long) -> Unit)? = null) {
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
        binding.pickerSecond.apply {
            minValue = 0
            maxValue = 59
            value = calendar[Calendar.SECOND]
        }
        builder.setPositiveButton("Đồng ý") { _, _ ->
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, binding.pickerHour.value)
                set(Calendar.MINUTE, binding.pickerMinute.value)
                set(Calendar.SECOND, binding.pickerSecond.value)
                set(Calendar.MILLISECOND, 0)
            }
            callback?.invoke(calendar.timeInMillis)
        }
        builder.setNegativeButton("Hủy") { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }
}