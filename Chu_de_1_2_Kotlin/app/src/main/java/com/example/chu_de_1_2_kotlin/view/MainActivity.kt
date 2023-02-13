package com.example.chu_de_1_2_kotlin.view

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.sax.Element
import android.util.Log
import android.view.View
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chu_de_1_2_kotlin.R
import com.example.chu_de_1_2_kotlin.databinding.ActivityMainBinding
import com.example.chu_de_1_2_kotlin.model.Student

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_SUCCESS = 1
    private var textSortIsSelected: Boolean = false

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
        binding.txtSortByName.setOnClickListener {
            selectedTextBack()
            selectedText(it)
        }
        binding.txtSortByYob.setOnClickListener {
            selectedTextBack()
            selectedText(it)
        }
        binding.txtSortByMajor.setOnClickListener {
            selectedTextBack()
            selectedText(it)
        }
        binding.txtSortByPhoneNumber.setOnClickListener {
            selectedTextBack()
            selectedText(it)
        }
    }

    private fun selectedTextBack() {
        binding.layoutOption.forEach { view ->
            view.background = resources.getDrawable(R.drawable.layout_search)
        }
    }

    private fun selectedText(view: View) {
        if (!textSortIsSelected) {
            view.background = resources.getDrawable(R.drawable.layout_selected)
            sort(view, listStudents)
            textSortIsSelected = true
        } else {
            view.background = resources.getDrawable(R.drawable.layout_search)
            textSortIsSelected = false
        }
    }

    private fun sort(view: View, list: ArrayList<Student>) {
        when (view.id) {
            R.id.txt_sort_by_name -> list.sortBy { it.name }
            R.id.txt_sort_by_yob -> list.sortBy { it.yob }
            R.id.txt_sort_by_major -> list.sortBy { it.major }
            R.id.txt_sort_by_phone_number -> list.sortBy { it.phoneNumber }
        }
        studentAdapter.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SUCCESS && resultCode == RESULT_OK) {
            studentAdapter.notifyDataSetChanged()
        }
    }
}