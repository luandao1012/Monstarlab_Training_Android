package com.example.chu_de_1_2_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chu_de_1_2_kotlin.model.Student
import com.example.chu_de_1_2_kotlin.view.MainActivity

class NewStudentViewModel : ViewModel() {
    fun addStudent(student: Student) {
        MainActivity.List.listStudents.add(student)
    }
}