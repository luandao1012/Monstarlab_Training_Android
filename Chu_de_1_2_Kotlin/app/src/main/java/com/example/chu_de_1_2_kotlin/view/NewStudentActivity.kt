package com.example.chu_de_1_2_kotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.chu_de_1_2_kotlin.databinding.ActivityNewStudentBinding
import com.example.chu_de_1_2_kotlin.model.Student
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
        binding.btnAddStudent.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val yob = binding.edtYob.text.toString().trim()
            val major = binding.edtMajor.text.toString().trim()
            val phoneNumber = binding.edtPhoneNumber.text.toString().trim()
            val type = binding.spinnerType.selectedItem.toString()
            val student = Student(name, yob, major, phoneNumber, type)
            viewModel.addStudent(
                student
            )
            setResult(RESULT_OK)
            onBackPressed()
        }
    }
}