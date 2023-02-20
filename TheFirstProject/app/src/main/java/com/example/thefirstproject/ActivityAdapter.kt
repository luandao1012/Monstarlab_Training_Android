package com.example.thefirstproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
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
            binding.txtNameActivity.text = listActivity[position].name
            binding.txtContentActivity.text = listActivity[position].content
            binding.txtPriceActivity.text = listActivity[position].price
            var price = listActivity[position].price.dropLast(1).toInt()
            if(price > 0){
                binding.txtPriceActivity.setTextColor(Color.parseColor("#00c55b"))
            } else {
                binding.txtPriceActivity.setTextColor(Color.parseColor("#1b1b1b"))
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