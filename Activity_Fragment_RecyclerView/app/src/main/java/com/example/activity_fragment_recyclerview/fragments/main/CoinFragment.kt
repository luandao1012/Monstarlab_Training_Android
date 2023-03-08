package com.example.activity_fragment_recyclerview.fragments.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.activity_fragment_recyclerview.databinding.FragmentCoinBinding
import kotlin.random.Random

class CoinFragment : Fragment() {
    private lateinit var binding: FragmentCoinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.fab.setOnClickListener {
            val random = Random.Default
            it.backgroundTintList = ColorStateList.valueOf(
                Color.rgb(
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
                )
            )
        }
        setFragmentResultListener(HomeFragment.HOME_FRAGMENT) { _, bundle ->
            val title = bundle.getString("title").toString()
            if (title.isNotEmpty()) {
                binding.tvCoinFragmentTitle.text = title
            }
        }
        binding.ivBackCoinFragment.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}