package com.example.thefirstproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.thefirstproject.databinding.ItemActivityBinding

class ActivityAdapter : Adapter<ActivityAdapter.ActivityViewHolder>() {
    private var listActivity = arrayListOf<Activity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Activity>) {
        listActivity = list
        notifyDataSetChanged()
    }

    inner class ActivityViewHolder(private val binding: ItemActivityBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvNameActivity.text = listActivity[position].name
            binding.tvContentActivity.text = listActivity[position].content
            binding.tvPriceActivity.text = listActivity[position].price
            val price = listActivity[position].price.dropLast(1).toInt()
            if (price > 0) {
                binding.tvPriceActivity.setTextColor(Color.parseColor("#00c55b"))
            } else {
                binding.tvPriceActivity.setTextColor(Color.parseColor("#1b1b1b"))
            }
            if (position == listActivity.lastIndex) {
                binding.dotted.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder(
            ItemActivityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listActivity.size
}