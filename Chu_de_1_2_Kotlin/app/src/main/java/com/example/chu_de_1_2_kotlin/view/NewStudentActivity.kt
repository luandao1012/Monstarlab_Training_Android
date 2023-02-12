package com.example.chu_de_1_2_kotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chu_de_1_2_kotlin.R
import com.example.chu_de_1_2_kotlin.databinding.ActivityNewStudentBinding

class NewStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStudentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewStudentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}