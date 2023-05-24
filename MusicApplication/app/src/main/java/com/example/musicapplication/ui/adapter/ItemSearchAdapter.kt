package com.example.musicapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapplication.databinding.ItemSearchBinding
import com.example.musicapplication.loadImage
import com.example.musicapplication.model.ItemSearch
import com.example.musicapplication.network.ApiBuilder

class ItemSearchAdapter : Adapter<ItemSearchAdapter.ItemSearchViewHolder>() {
    private var listItem = listOf<ItemSearch>()
    private var callbackOnClick: ((itemSearch: ItemSearch) -> Unit)? = null
    fun setData(list: List<ItemSearch>) {
        listItem = list
        notifyDataSetChanged()
    }

    fun setItemOnClick(callback: (itemSearch: ItemSearch) -> Unit) {
        callbackOnClick = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSearchViewHolder {
        val itemSearchViewHolder = ItemSearchViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        itemSearchViewHolder.bindListeners()
        return itemSearchViewHolder
    }

    override fun getItemCount(): Int = listItem.size
    override fun onBindViewHolder(holder: ItemSearchViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemSearchViewHolder(private val binding: ItemSearchBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            val song = listItem[position]
            binding.ivItemMp3.loadImage(ApiBuilder.IMAGE_URL + song.image)
            binding.tvItemName.text = song.name
            binding.tvItemSingle.text = song.single
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                if (adapterPosition != -1) {
                    callbackOnClick?.invoke(listItem[adapterPosition])
                }
            }
        }
    }
}