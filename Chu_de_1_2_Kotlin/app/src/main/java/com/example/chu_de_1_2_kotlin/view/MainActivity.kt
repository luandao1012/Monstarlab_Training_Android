package com.example.chu_de_1_2_kotlin.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chu_de_1_2_kotlin.databinding.ActivityMainBinding
import com.example.chu_de_1_2_kotlin.model.Student

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_SUCCESS = 1

    companion object List {
        lateinit var listStudents: ArrayList<Student>
    }

    private lateinit var studentAdapter: StudentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initOnClickListener()
    }

    private fun init() {
        listStudents = arrayListOf()
        studentAdapter = StudentAdapter()
        studentAdapter.setData(listStudents)
        binding.rvStudent.layoutManager = LinearLayoutManager(this)
        binding.rvStudent.adapter = studentAdapter
    }

    private fun initOnClickListener() {
        binding.btnActivityAdd.setOnClickListener {
            val intent = Intent(this, NewStudentActivity::class.java)
            startActivityForResult(intent, REQUEST_SUCCESS)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SUCCESS && resultCode == RESULT_OK) {
            studentAdapter.notifyDataSetChanged()
        }
    }
}