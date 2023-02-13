package com.example.chu_de_1_2_kotlin.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chu_de_1_2_kotlin.R
import com.example.chu_de_1_2_kotlin.databinding.ActivityMainBinding
import com.example.chu_de_1_2_kotlin.model.Student
import com.example.chu_de_1_2_kotlin.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_SUCCESS = 1
    private var textSortIsSelected: Boolean = false
    private var textFilterIsSelected: Boolean = false
    private lateinit var mainViewModel: MainViewModel

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
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun initOnClickListener() {
        binding.btnActivityAdd.setOnClickListener {
            val intent = Intent(this, NewStudentActivity::class.java)
            startActivityForResult(intent, REQUEST_SUCCESS)
        }
        binding.txtSortByName.setOnClickListener {
            selectedTextBack()
            selectedText(it, binding.txtSortByName.text.toString())
        }
        binding.txtSortByYob.setOnClickListener {
            selectedTextBack()
            selectedText(it, binding.txtSortByYob.text.toString())
        }
        binding.txtSortByPhoneNumber.setOnClickListener {
            selectedTextBack()
            selectedText(it, binding.txtSortByPhoneNumber.text.toString())
        }
        binding.txtSortByMajor.setOnClickListener {
            selectedTextBack()
            selectedText(it, binding.txtSortByMajor.text.toString())
        }
        binding.txtFilterByCollege.setOnClickListener {
            selectionTextFilter(it, binding.txtFilterByUniversity, "Cao đẳng")
        }
        binding.txtFilterByUniversity.setOnClickListener {
            selectionTextFilter(it, binding.txtFilterByCollege, "Đại học")
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectedTextBack() {
        binding.layoutOption.forEach { view ->
            if (view.id != R.id.txt_filter_by_college && view.id != R.id.txt_filter_by_university) {
                view.background = resources.getDrawable(R.drawable.layout_search)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectedText(view: View, element: String) {
        if (!textSortIsSelected) {
            view.background = resources.getDrawable(R.drawable.layout_selected)
            sort(element, studentAdapter.getData())
            textSortIsSelected = true
        } else {
            view.background = resources.getDrawable(R.drawable.layout_search)
            textSortIsSelected = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sort(element: String, list: ArrayList<Student>) {
        mainViewModel.sort(element, list)
        studentAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filter(element: String, list: ArrayList<Student>) {
        val listFilter = mainViewModel.filter(element, list)
        studentAdapter.setData(listFilter)
        studentAdapter.notifyDataSetChanged()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectionTextFilter(view: View, otherView: View, element: String) {
        if (!textFilterIsSelected) {
            view.background = resources.getDrawable(R.drawable.layout_selected)
            filter(element, listStudents)
            textFilterIsSelected = true
        } else {
            studentAdapter.setData(listStudents)
            view.background = resources.getDrawable(R.drawable.layout_search)
            textFilterIsSelected = false
        }
        otherView.background = resources.getDrawable(R.drawable.layout_search)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SUCCESS && resultCode == RESULT_OK) {
            studentAdapter.notifyDataSetChanged()
        }
    }
}