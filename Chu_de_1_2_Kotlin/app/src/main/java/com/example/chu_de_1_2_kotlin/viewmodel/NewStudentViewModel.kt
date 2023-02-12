package com.example.chu_de_1_2_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chu_de_1_2_kotlin.model.Student
import com.example.chu_de_1_2_kotlin.view.MainActivity

class NewStudentViewModel : ViewModel() {
    fun addStudent(
        name: String,
        yob: String,
        major: String,
        phoneNumber: String,
        type: String
    ) {
        MainActivity.Companion.listStudents.add(Student(name, yob, major, phoneNumber, type))
    }
}