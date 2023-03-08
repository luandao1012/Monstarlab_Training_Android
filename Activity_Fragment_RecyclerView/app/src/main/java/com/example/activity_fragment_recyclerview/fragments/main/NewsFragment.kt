package com.example.activity_fragment_recyclerview.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.adapters.NewsRVAdapter
import com.example.activity_fragment_recyclerview.data.DataNews
import com.example.activity_fragment_recyclerview.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        childFragmentManager.commit {
            add(R.id.fragment_container_view_news_fragment, NewsHomeFragment())
        }
    }

    private fun initListeners() {
        binding.ivBackNewsFragment.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun setTitleNews() {
        binding.apply {
            tvTitleNews.text = "NEWS"
            ivMenuCoinFragment.visibility = View.VISIBLE
        }
    }

    fun setTitleNewsArticle() {
        binding.apply {
            tvTitleNews.text = "EDITORIAL NEWS"
            ivMenuCoinFragment.visibility = View.GONE
        }
    }
}