package com.example.roomdatabase.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.databinding.ItemDiaryBinding
import java.util.*

class NoteListAdapter : Adapter<NoteListAdapter.DiaryListViewHolder>() {
    private var listData = listOf<Note>()
    private var callbackOnClick: ((date: Long) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Note>) {
        listData = list
        notifyDataSetChanged()
    }

    fun setOnClickCallback(callback: (date: Long) -> Unit) {
        callbackOnClick = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryListViewHolder {
        val diaryListViewHolder = DiaryListViewHolder(
            ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        diaryListViewHolder.bindListeners()
        return diaryListViewHolder
    }

    override fun onBindViewHolder(holder: DiaryListViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listData.size

    inner class DiaryListViewHolder(private val binding: ItemDiaryBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val calendar = Calendar.getInstance()
            val diary = listData[position]
            calendar.timeInMillis = diary.date
            binding.tvDateTime.text =
                "Ngày ${calendar[Calendar.DATE]} Tháng ${calendar[Calendar.MONTH] + 1} ${calendar[Calendar.YEAR]}"
            binding.tvContent.text = diary.content
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                val diary = if (adapterPosition != -1) listData[adapterPosition] else null
                diary?.let {
                    callbackOnClick?.invoke(it.date)
                }
            }
        }
    }
}