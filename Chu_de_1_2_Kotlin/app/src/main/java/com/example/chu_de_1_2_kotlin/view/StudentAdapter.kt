package com.example.chu_de_1_2_kotlin.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chu_de_1_2_kotlin.databinding.ItemStudentBinding
import com.example.chu_de_1_2_kotlin.model.Student

class StudentAdapter : Adapter<StudentAdapter.StudentViewHolder>() {
    private var listStudent = arrayListOf<Student>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Student>) {
        listStudent = list
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<Student> {
        return listStudent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listStudent.size

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.txtName.text = listStudent[position].name
            binding.txtYob.text = listStudent[position].yob
            binding.txtPhoneNumber.text = listStudent[position].phoneNumber
            binding.txtMajor.text = listStudent[position].major
            binding.txtType.text = listStudent[position].type
        }
    }
}