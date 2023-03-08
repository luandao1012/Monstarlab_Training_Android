package com.example.activity_fragment_recyclerview.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.adapters.MenuRVAdapter
import com.example.activity_fragment_recyclerview.data.DataMenu
import com.example.activity_fragment_recyclerview.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private var menuRVAdapter: MenuRVAdapter? = null
    private var listMenuItems = arrayListOf<DataMenu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addData()
        initViews()
        initListeners()
    }

    private fun initListeners() {
        binding.ivBackMenuFragment.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initViews() {
        menuRVAdapter = MenuRVAdapter()
        menuRVAdapter?.setData(listMenuItems)
        binding.rvMenuFragment.adapter = menuRVAdapter
    }

    private fun addData() {
        listMenuItems += DataMenu(0, "MARY OLSON", MenuRVAdapter.TYPE_1)
        listMenuItems += DataMenu(R.drawable.icons_8_alarm, "Alerts", MenuRVAdapter.TYPE_2)
        listMenuItems += DataMenu(
            R.drawable.icons_8_left_and_right_arrows,
            "Predictions",
            MenuRVAdapter.TYPE_2
        )
        listMenuItems += DataMenu(R.drawable.icons_8_pin, "Saved elements", MenuRVAdapter.TYPE_2)
        listMenuItems += DataMenu(R.drawable.icons_8_no_entry, "Remove Ads", MenuRVAdapter.TYPE_2)
        listMenuItems += DataMenu(R.drawable.icons_8_no_entry, "Tools", MenuRVAdapter.TYPE_3)
        listMenuItems += DataMenu(
            R.drawable.icons_8_profit_2,
            "Select Stocks",
            MenuRVAdapter.TYPE_4
        )
        listMenuItems += DataMenu(
            R.drawable.icons_8_swap,
            "Currency Exchange",
            MenuRVAdapter.TYPE_4
        )
        listMenuItems += DataMenu(R.drawable.icons_8_video_call, "Webinar", MenuRVAdapter.TYPE_4)
        listMenuItems += DataMenu(R.drawable.icons_8_rent, "Best Broker", MenuRVAdapter.TYPE_4)
        listMenuItems += DataMenu(R.drawable.icons_8_no_entry, "Markets", MenuRVAdapter.TYPE_3)
        listMenuItems += DataMenu(
            R.drawable.icons_8_profit_2,
            "Select Stocks",
            MenuRVAdapter.TYPE_4
        )
        listMenuItems += DataMenu(
            R.drawable.icons_8_swap,
            "Currency Exchange",
            MenuRVAdapter.TYPE_4
        )
        listMenuItems += DataMenu(R.drawable.icons_8_video_call, "Webinar", MenuRVAdapter.TYPE_4)
        listMenuItems += DataMenu(R.drawable.icons_8_rent, "Best Broker", MenuRVAdapter.TYPE_4)
    }
}