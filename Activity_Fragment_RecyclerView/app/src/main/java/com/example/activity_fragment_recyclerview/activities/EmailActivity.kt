package com.example.activity_fragment_recyclerview.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.databinding.ActivityEmailBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

@SuppressLint("SetTextI18n")
class EmailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEmailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        countdown()
        binding.btnResendEmail.setOnClickListener {
            startActivity(Intent(this, ConfirmPasswordChangedActivity::class.java))
        }
    }

    private fun countdown() {
        lifecycleScope.launch {
            var time = 5
            binding.btnResendEmail.isEnabled = false
            repeat(6) {
                delay(1000)
                binding.tvTimeResendEmail.text = "Wait $time seconds before sending it"
                time--
            }
            binding.btnResendEmail.isEnabled = true
        }
    }
}