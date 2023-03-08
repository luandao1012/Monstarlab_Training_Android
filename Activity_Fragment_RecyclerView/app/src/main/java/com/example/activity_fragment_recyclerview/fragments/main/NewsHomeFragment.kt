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
import com.example.activity_fragment_recyclerview.databinding.FragmentNewsHomeBinding

class NewsHomeFragment : Fragment() {
    private var newsRVAdapter: NewsRVAdapter? = null
    private lateinit var binding: FragmentNewsHomeBinding
    private var listNewsItems = arrayListOf<DataNews>()

    companion object {
        const val KEY_NEWS_HOME_FRAGMENT = "News Home Fragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initListeners() {
        newsRVAdapter?.setOnClickItemListener {
            (parentFragment as NewsFragment).setTitleNewsArticle()
            val newsArticleFragment = NewsArticleFragment()
            val bundle = Bundle().apply {
                putString("title", listNewsItems[it].title)
                putString("date", listNewsItems[it].date)
            }
            newsArticleFragment.arguments = bundle
            parentFragmentManager.apply {
                commit {
                    add(R.id.fragment_container_view_news_fragment, newsArticleFragment)
                    addToBackStack(null)
                }
            }
        }
    }

    private fun initViews() {
        addData()
        newsRVAdapter = NewsRVAdapter()
        newsRVAdapter?.setData(listNewsItems)
        binding.rvNewsFragment.adapter = newsRVAdapter
    }

    private fun addData() {
        listNewsItems += DataNews(R.drawable.image_news_1, "ALT -3,87%", "ATLANTIA", "3 Sept 2020")
        listNewsItems += DataNews(R.drawable.image_news_2, "HKD -2,13%", "XIAOMI", "2 Sept 2020")
        listNewsItems += DataNews(R.drawable.image_news_1, "AAPL -0,91%", "APPLE", "1 Sept 2020")
    }
}