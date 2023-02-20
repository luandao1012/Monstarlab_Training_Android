package com.example.thefirstproject

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.thefirstproject.databinding.ItemEnviarDeNuevoBinding

class EnviarAdapter : Adapter<EnviarAdapter.EviarViewHolder>() {
    private var listEnviar = arrayListOf<Enviar>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Enviar>) {
        listEnviar = list
        notifyDataSetChanged()
    }

    inner class EviarViewHolder(private val binding: ItemEnviarDeNuevoBinding) :
        ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.imgAvatar.setImageResource(listEnviar[position].img)
            binding.txtName.text = listEnviar[position].name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EviarViewHolder {
        return EviarViewHolder(
            ItemEnviarDeNuevoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: EviarViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = listEnviar.size
}