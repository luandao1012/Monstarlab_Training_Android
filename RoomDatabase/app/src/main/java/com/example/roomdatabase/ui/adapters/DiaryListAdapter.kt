package com.example.roomdatabase.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.roomdatabase.data.Diary
import com.example.roomdatabase.databinding.ItemDiaryBinding
import java.util.*

class DiaryListAdapter : Adapter<DiaryListAdapter.DiaryListViewHolder>() {
    private var listDiary = listOf<Diary>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Diary>) {
        listDiary = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryListViewHolder {
        val diaryListViewHolder = DiaryListViewHolder(
            ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        return diaryListViewHolder
    }

    override fun onBindViewHolder(holder: DiaryListViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listDiary.size

    inner class DiaryListViewHolder(private val binding: ItemDiaryBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val calendar = Calendar.getInstance()
            val diary = listDiary[position]
            calendar.timeInMillis = diary.date
            binding.tvDateTime.text =
                "Ngày ${calendar[Calendar.DATE]} Tháng ${calendar[Calendar.MONTH] + 1} ${calendar[Calendar.YEAR]}"
            binding.tvContent.text = diary.content
        }
    }
}