package com.example.activity_fragment_recyclerview.fragments.main

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.activity_fragment_recyclerview.activities.MainActivity
import com.example.activity_fragment_recyclerview.adapters.HomeRVAdapter
import com.example.activity_fragment_recyclerview.customview.RecyclerViewItemTouchHelper
import com.example.activity_fragment_recyclerview.data.DataHome
import com.example.activity_fragment_recyclerview.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

@SuppressLint("NotifyDataSetChanged")
class HomeFragment() : Fragment(), OnClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var itemTouchHelper: ItemTouchHelper.SimpleCallback
    private var homeRVAdapter: HomeRVAdapter? = null
    private var snackBar: Snackbar? = null
    private var undoObject: Pair<Int, DataHome>? = null

    companion object {
        const val HOME_FRAGMENT = "Home Fragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        homeRVAdapter = HomeRVAdapter()
        addData()
        snackBar = Snackbar.make(binding.root, "Delete", Snackbar.LENGTH_LONG).apply {
            setActionTextColor(Color.YELLOW)
            setAction("Undo") {
                undoObject?.let { homeRVAdapter?.getData()?.add(it.first, it.second) }
                homeRVAdapter?.notifyDataSetChanged()
            }
        }
        binding.rvHomeFragment.adapter = homeRVAdapter
        itemTouchHelper = RecyclerViewItemTouchHelper(0, ItemTouchHelper.RIGHT)
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvHomeFragment)
    }

    private fun initListeners() {
        binding.fab.setOnClickListener(this)
        homeRVAdapter?.setOnClickItemListener {
            (activity as MainActivity).showFragment(1)
            val bundle = Bundle().apply {
                putString("title", homeRVAdapter?.getData()?.get(it)?.name)
            }
            setFragmentResult(HOME_FRAGMENT, bundle)
        }
        binding.nestedSv.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY && binding.fab.isShown) {
                binding.fab.hide()
            } else if (scrollY < oldScrollY && !binding.fab.isShown) {
                binding.fab.show()
            }
        }
        binding.btnLoadMore.setOnClickListener(this)
        (itemTouchHelper as RecyclerViewItemTouchHelper).setSwipedListener { data ->
            val index = data.adapterPosition
            val homeData = homeRVAdapter?.getData()?.get(index)
            undoObject = Pair(index, homeData!!)
            homeRVAdapter?.getData()?.removeAt(index)
            homeRVAdapter?.notifyDataSetChanged()
            snackBar?.show()
        }
    }

    private fun addData() {
        val listHomes = arrayListOf<DataHome>()
        listHomes += DataHome("DOWN JONES", "NYSE")
        listHomes += DataHome("FTSE 100", "LONDON")
        listHomes += DataHome("IBEX 35", "MADRID")
        listHomes += DataHome("DAX", "XETRA")
        listHomes += DataHome("DOWN JONES", "NYSE")
        listHomes += DataHome("FTSE 100", "LONDON")
        listHomes += DataHome("IBEX 35", "MADRID")
        listHomes += DataHome("DAX", "XETRA")
        listHomes += DataHome("DOWN JONES", "NYSE")
        listHomes += DataHome("FTSE 100", "LONDON")
        homeRVAdapter?.addData(listHomes)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.fab -> {
                val random = Random.Default
                view.backgroundTintList = ColorStateList.valueOf(
                    Color.rgb(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                    )
                )
            }
            binding.btnLoadMore -> {
                addData()
            }
        }
    }
}