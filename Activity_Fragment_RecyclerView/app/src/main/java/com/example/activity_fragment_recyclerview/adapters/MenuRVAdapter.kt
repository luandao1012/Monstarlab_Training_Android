package com.example.activity_fragment_recyclerview.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.activity_fragment_recyclerview.data.DataMenu
import com.example.activity_fragment_recyclerview.databinding.Item1RvMenuFragmentBinding
import com.example.activity_fragment_recyclerview.databinding.Item2RvMenuFragmentBinding
import com.example.activity_fragment_recyclerview.databinding.Item3RvMenuFragmentBinding
import com.example.activity_fragment_recyclerview.databinding.Item4RvMenuFragmentBinding

@SuppressLint("NotifyDataSetChanged")
class MenuRVAdapter : Adapter<ViewHolder>() {
    companion object {
        const val TYPE_1 = 1
        const val TYPE_2 = 2
        const val TYPE_3 = 3
        const val TYPE_4 = 4
    }

    private var listData = arrayListOf<DataMenu>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            TYPE_1 -> {
                return ProfileViewHolder(
                    Item1RvMenuFragmentBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            TYPE_2 -> {
                return AlertsViewHolder(
                    Item2RvMenuFragmentBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            TYPE_3 -> {
                return ToolsViewHolder(
                    Item3RvMenuFragmentBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                return ItemToolsViewHolder(
                    Item4RvMenuFragmentBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ProfileViewHolder -> holder.bind(position)
            is AlertsViewHolder -> holder.bind(position)
            is ToolsViewHolder -> holder.bind(position)
            is ItemToolsViewHolder -> holder.bind(position)
        }
    }

    override fun getItemCount(): Int = listData.size

    override fun getItemViewType(position: Int): Int = listData[position].type

    fun setData(listMenuItems: ArrayList<DataMenu>) {
        listData = listMenuItems
        notifyDataSetChanged()
    }

    inner class ProfileViewHolder(private val binding: Item1RvMenuFragmentBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNameMenuItem.text = listData[position].name
            }
        }
    }

    inner class AlertsViewHolder(private val binding: Item2RvMenuFragmentBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNameMenuItem.text = listData[position].name
                ivItemMenu.setImageResource(listData[position].image)
            }
        }
    }

    inner class ToolsViewHolder(private val binding: Item3RvMenuFragmentBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNameItemMenu.text = listData[position].name
            }
        }
    }

    inner class ItemToolsViewHolder(private val binding: Item4RvMenuFragmentBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNameMenuItem.text = listData[position].name
                ivItemMenu.setImageResource(listData[position].image)
            }
        }
    }

}