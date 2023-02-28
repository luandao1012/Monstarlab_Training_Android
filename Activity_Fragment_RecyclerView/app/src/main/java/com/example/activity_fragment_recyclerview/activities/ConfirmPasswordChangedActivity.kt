package com.example.activity_fragment_recyclerview.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.activity_fragment_recyclerview.databinding.ActivityConfirmPasswordChangedBinding

class ConfirmPasswordChangedActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConfirmPasswordChangedBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLoginConfirmPassword.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("password", "123")
            startActivity(intent)
        }
    }
}