package com.example.service_broadcast

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.service_broadcast.databinding.ItemSongBinding

@SuppressLint("NotifyDataSetChanged")
class SongAdapter : Adapter<SongAdapter.SongViewHolder>() {
    private var listMusic = arrayListOf<Song>()
    private var callbackOnClick: ((position: Int) -> Unit)? = null
    private var mp3Position = -1
    fun setData(list: List<Song>) {
        listMusic = list as ArrayList<Song>
        notifyDataSetChanged()
    }

    fun setMp3Position(position: Int) {
        mp3Position = position
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
            binding.ivPlaying.visibility = if (position == mp3Position) View.VISIBLE else View.GONE
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                callbackOnClick?.invoke(adapterPosition)
            }
        }
    }
}