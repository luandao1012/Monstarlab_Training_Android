package com.example.excercisethread

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.example.excercisethread.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.random.Random

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var number = 0
    private var jobIncrease: Job? = null
    private var jobDecrease: Job? = null
    private var jobReturn: Job? = null
    private var oldY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initOnclick()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initOnclick() {
        binding.btnIncrease.setOnClickListener {
            click("up")
        }
        binding.btnDecrease.setOnClickListener {
            click("down")
        }
        binding.btnIncrease.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                number = binding.tvShowNumber.text.toString().toInt()
                jobIncrease?.cancel()
                countdown()
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                jobReturn?.cancel()
                increase()
            }
            false
        }
        binding.btnDecrease.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                number = binding.tvShowNumber.text.toString().toInt()
                jobDecrease?.cancel()
                countdown()
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                jobReturn?.cancel()
                decrease()
            }
            false
        }
        binding.tvShowNumber.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldY = event.y
                    jobReturn?.cancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.y - oldY
                    val tvNumber = number - (deltaY / 10).toInt()
                    changeText(tvNumber)
                }
                MotionEvent.ACTION_UP -> {
                    number = binding.tvShowNumber.text.toString().toInt()
                    countdown()
                }
            }
            true
        }
    }

    private fun click(string: String) {
        number = binding.tvShowNumber.text.toString().toInt()
        when (string) {
            "up" -> number++
            "down" -> number--
        }
        changeText(number)
    }

    private fun increase() {
        jobIncrease = GlobalScope.launch {
            delay(1000)
            while (true) {
                delay(10)
                number++
                changeText(number)
            }
        }
    }

    private fun decrease() {
        jobDecrease = GlobalScope.launch {
            delay(1000)
            while (true) {
                delay(10)
                number--
                changeText(number)
            }
        }
    }

    private fun countdown() {
        jobReturn = GlobalScope.launch {
            delay(1000)
            if (number > 0) {
                while (number > 0) {
                    number--
                    delay(50)
                    changeText(number)
                }
            } else {
                while (number < 0) {
                    number++
                    delay(50)
                    changeText(number)
                }
            }
        }
    }

    private fun changeText(num: Int) {
        binding.tvShowNumber.text = num.toString()
        if (num != 0 && num % 100 == 0) {
            val random = Random.Default
            binding.tvShowNumber.setTextColor(
                Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
            )
        }
    }
}