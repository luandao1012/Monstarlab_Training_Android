package com.example.chu_de_1_2_kotlin.viewmodel

import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.ViewModel
import com.example.chu_de_1_2_kotlin.R
import com.example.chu_de_1_2_kotlin.model.Student
import com.example.chu_de_1_2_kotlin.view.MainActivity

class MainViewModel : ViewModel() {

    fun sort(element: String, list: ArrayList<Student>) {
        when (element) {
            "Tên" -> list.sortBy { it.name }
            "Năm sinh" -> list.sortBy { it.yob }
            "Chuyên ngành" -> list.sortBy { it.major }
            "Số điện thoại" -> list.sortBy { it.phoneNumber }
        }
    }

    fun filter(element: String, list: ArrayList<Student>): ArrayList<Student> {
        var listFilter = arrayListOf<Student>()
        listFilter = list.filter { it.type == element } as ArrayList<Student>
        return listFilter
    }
}