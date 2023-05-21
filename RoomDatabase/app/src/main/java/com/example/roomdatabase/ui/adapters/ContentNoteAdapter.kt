package com.example.roomdatabase.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.data.Type
import com.example.roomdatabase.databinding.ItemContentDiaryBinding
import com.example.roomdatabase.databinding.ItemContentReminderBinding

class ContentNoteAdapter : Adapter<ViewHolder>() {
    private var listContent = listOf<Note>()
    private var callbackOnClick: ((note: Note) -> Unit)? = null
    fun setData(list: List<Note>) {
        listContent = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(callback: (note: Note) -> Unit) {
        callbackOnClick = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            Type.DIARY.ordinal -> {
                val diaryContentViewHolder = DiaryContentViewHolder(
                    ItemContentDiaryBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
                diaryContentViewHolder.bindListeners()
                return diaryContentViewHolder
            }

            else -> {
                val reminderContentViewHolder = ReminderContentViewHolder(
                    ItemContentReminderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
                reminderContentViewHolder.bindListeners()
                return reminderContentViewHolder
            }
        }
    }

    override fun getItemCount(): Int = listContent.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is DiaryContentViewHolder -> holder.bind(position)
            is ReminderContentViewHolder -> holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int = listContent[position].type.ordinal

    inner class DiaryContentViewHolder(private val binding: ItemContentDiaryBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvContentDiary.isSelected = true
            binding.tvContentDiary.text = listContent[position].content
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    callbackOnClick?.invoke(listContent[adapterPosition])
                }
            }
        }
    }

    inner class ReminderContentViewHolder(private val binding: ItemContentReminderBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvContentReminder.text = listContent[position].content
            binding.cbReminder.isChecked = listContent[position].isCompleted == true
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    callbackOnClick?.invoke(listContent[adapterPosition])
                }
            }
        }
    }
}