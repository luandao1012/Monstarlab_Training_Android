package com.example.service_broadcast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.service_broadcast.databinding.ItemSongBinding

class SongAdapter : Adapter<SongAdapter.SongViewHolder>() {
    private var listMusic = arrayListOf<Song>()
    private var callbackOnClick: ((position: Int) -> Unit)? = null
    fun setData(list: List<Song>) {
        listMusic = list as ArrayList<Song>
        notifyDataSetChanged()
    }

    fun setOnClickCallback(callback: ((position: Int) -> Unit)? = null) {
        callbackOnClick = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val songViewHolder = SongViewHolder(
            ItemSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        songViewHolder.bindListeners()
        return songViewHolder
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listMusic.size

    inner class SongViewHolder(private val binding: ItemSongBinding) : ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.name.text = listMusic[position].name
            binding.signer.text = listMusic[position].singer
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                callbackOnClick?.invoke(adapterPosition)
            }
        }
    }
}