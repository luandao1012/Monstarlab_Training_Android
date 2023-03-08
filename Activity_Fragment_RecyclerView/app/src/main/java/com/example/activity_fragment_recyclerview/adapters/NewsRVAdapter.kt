package com.example.activity_fragment_recyclerview.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.activity_fragment_recyclerview.data.DataNews
import com.example.activity_fragment_recyclerview.databinding.ItemRvNewsFragmentBinding

@SuppressLint("NotifyDataSetChanged")
class NewsRVAdapter : Adapter<NewsRVAdapter.NewsViewHolder>() {
    private var listNews = arrayListOf<DataNews>()
    private var callbackListener: ((position: Int) -> Unit)? = null

    fun setData(list: ArrayList<DataNews>) {
        listNews = list
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(private val binding: ItemRvNewsFragmentBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                ivNewsItem.setImageResource(listNews[position].image)
                tvNewsItemChange.text = listNews[position].change
                tvDateNewsItem.text = listNews[position].date
                tvNewsItemTitle.text = listNews[position].title
            }
        }
    }

    fun setOnClickItemListener(callback: (position: Int) -> Unit) {
        callbackListener = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =
            ItemRvNewsFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val newsViewHolder = NewsViewHolder(binding)
        binding.root.setOnClickListener {
            callbackListener?.invoke(newsViewHolder.adapterPosition)
        }
        return newsViewHolder
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listNews.size
}