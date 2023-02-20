package com.example.thefirstproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thefirstproject.databinding.ActivityCardsBinding

class CardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardsBinding
    private lateinit var cardsListAdapter: CardsListAdapter
    private lateinit var listCards: ArrayList<CardInfo>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        cardsListAdapter = CardsListAdapter()
        listCards = arrayListOf()
        cardsListAdapter.setData(listCards)
        setData()
        binding.rvCardsList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = cardsListAdapter
        }
    }

    private fun setData() {
        listCards += CardInfo(R.drawable.mastercard_2, "Mastercard", "****9889")
        listCards += CardInfo(R.drawable.mastercard_2, "Mastercard", "****9889")
        listCards += CardInfo(R.drawable.combined_shape, "Visa black", "****8764")
        listCards += CardInfo(R.drawable.combined_shape, "Visa black", "****8764")
    }
}