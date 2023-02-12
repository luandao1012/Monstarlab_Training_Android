package com.example.chu_de_1_2_kotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.chu_de_1_2_kotlin.R
import com.example.chu_de_1_2_kotlin.databinding.ActivityNewStudentBinding
import com.example.chu_de_1_2_kotlin.viewmodel.NewStudentViewModel

class NewStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStudentBinding
    private lateinit var viewModel: NewStudentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewStudentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initOnClickListener()
    }

    private fun init() {
        viewModel = ViewModelProvider(this)[NewStudentViewModel::class.java]
    }

    private fun initOnClickListener() {
        val name = binding.edtName.text.toString().trim()
        val yob = binding.edtName.text.toString().trim()
        val major = binding.edtName.text.toString().trim()
        val phoneNumber = binding.edtName.text.toString().trim()
        val type = binding.edtName.text.toString().trim()
        viewModel.addStudent(name, yob, major, phoneNumber, type)
    }
}