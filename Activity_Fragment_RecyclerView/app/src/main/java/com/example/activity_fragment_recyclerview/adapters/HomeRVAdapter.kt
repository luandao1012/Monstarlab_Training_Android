package com.example.activity_fragment_recyclerview.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.activity_fragment_recyclerview.data.DataHome
import com.example.activity_fragment_recyclerview.databinding.ItemRvHomeBinding

@SuppressLint("NotifyDataSetChanged")
class HomeRVAdapter : Adapter<HomeRVAdapter.HomeViewHolder>() {
    private var listHome = arrayListOf<DataHome>()
    private var callbackListener: ((pos: Int) -> Unit)? = null

    fun setData(list: ArrayList<DataHome>) {
        listHome = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemRvHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val homeViewHolder = HomeViewHolder(binding)
        binding.root.setOnClickListener {
            callbackListener?.invoke(homeViewHolder.adapterPosition)
        }
        return homeViewHolder
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listHome.size

    fun setOnClickItemListener(callback: (pos: Int) -> Unit) {
        callbackListener = callback
    }

    inner class HomeViewHolder(private val binding: ItemRvHomeBinding) : ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvItemNameRvHome.text = listHome[position].name
                tvItemAddressRvHome.text = listHome[position].address
            }
        }

        fun view(): View = binding.layoutForegroundRvHomeItem
    }
}