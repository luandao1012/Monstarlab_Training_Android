package com.example.activity_fragment_recyclerview.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.activity_fragment_recyclerview.databinding.FragmentNewsArticleBinding

class NewsArticleFragment : Fragment() {
    private lateinit var binding: FragmentNewsArticleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsArticleBinding.inflate(inflater, container, false)
        val title = this.arguments?.getString("title").toString()
        val date = this.arguments?.getString("date").toString()
        if (title.isNotEmpty() && date.isNotEmpty()) {
            binding.apply {
                tvNewsArticleTitle.text = title
                tvDateNewsArticle.text = date
            }
        }
        return binding.root
    }
}