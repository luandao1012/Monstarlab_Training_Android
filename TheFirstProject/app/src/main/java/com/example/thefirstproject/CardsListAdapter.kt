package com.example.thefirstproject

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.transition.Visibility
import com.example.thefirstproject.databinding.ItemCardBinding

class CardsListAdapter : Adapter<CardsListAdapter.CardsViewHolder>() {
    private var listCard = arrayListOf<CardInfo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<CardInfo>) {
        listCard = list
        notifyDataSetChanged()
    }

    inner class CardsViewHolder(private val binding: ItemCardBinding) : ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.ivCardType.setImageResource(listCard[position].image)
            binding.tvCardName.text = listCard[position].name
            binding.tvCardNumber.text = listCard[position].number
        }

        fun bindOnclick() {
            binding.ivShowOption.setOnClickListener {
                binding.llOptionCards.visibility = View.VISIBLE
                binding.ivShowOption.visibility = View.GONE
            }
            binding.ivClearOption.setOnClickListener {
                binding.llOptionCards.visibility = View.GONE
                binding.ivShowOption.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val cardsViewHolder = CardsViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        cardsViewHolder.bindOnclick()
        return cardsViewHolder
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listCard.size
}