package com.example.musicapplication.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicapplication.databinding.ItemMp3OfflineBinding
import com.example.musicapplication.formatSongTime
import com.example.musicapplication.loadImage
import com.example.musicapplication.model.Song

@SuppressLint("NotifyDataSetChanged")
class SongOfflineAdapter : Adapter<SongOfflineAdapter.SongOffline>() {
    private var songList = listOf<Song>()
    private var callbackOnClick: ((position: Int) -> Unit)? = null
    private var callbackDownload: ((song: Song) -> Unit)? = null
    private var callbackFavourite: ((song: Song) -> Unit)? = null

    fun setData(list: List<Song>) {
        songList = list
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: ((position: Int) -> Unit)? = null) {
        callbackOnClick = callback
    }

    fun setDownload(callback: ((song: Song) -> Unit)? = null) {
        callbackDownload = callback
    }

    fun setFavourite(callback: ((song: Song) -> Unit)? = null) {
        callbackFavourite = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongOffline {
        val songViewHolder = SongOffline(
            ItemMp3OfflineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        songViewHolder.bindListeners()
        return songViewHolder
    }

    override fun getItemCount(): Int = songList.size

    override fun onBindViewHolder(holder: SongOffline, position: Int) {
        holder.bind(position)
    }

    inner class SongOffline(private val binding: ItemMp3OfflineBinding) : ViewHolder(binding.root) {
        fun bind(position: Int) {
            val song = songList[position]
            binding.ivItemMp3.loadImage(song.image)
            binding.tvItemName.text = song.name
            binding.tvItemSingle.text = song.singer
            binding.tvItemTime.text = song.duration.formatSongTime()
        }

        fun bindListeners() {
            binding.root.setOnClickListener {
                callbackOnClick?.invoke(adapterPosition)
            }
        }
    }
}