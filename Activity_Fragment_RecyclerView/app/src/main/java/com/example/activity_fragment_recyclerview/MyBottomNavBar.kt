package com.example.activity_fragment_recyclerview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.example.activity_fragment_recyclerview.databinding.MyBottomNavBarBinding

class MyBottomNavBar @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    LinearLayout(context, attr) {
    private val binding: MyBottomNavBarBinding
    private var indexSelectedItem: Int = 0
    private var callback: ((int: Int) -> Unit)? = null

    init {
        binding = MyBottomNavBarBinding.inflate(LayoutInflater.from(context), this, true)
        changeItemState(binding.ivIncreaseNav, true)
        binding.llBottomNavBar.children.forEachIndexed { index, view ->
            view.setOnClickListener { onItemClick(index, view as ImageView) }
        }
    }

    private fun onItemClick(index: Int, view: ImageView) {
        if (index != indexSelectedItem) {
            callback?.let { it(index) }
            val imageView = binding.llBottomNavBar.getChildAt(indexSelectedItem) as ImageView
            changeItemState(imageView, false)
            changeItemState(view, true)
            indexSelectedItem = index
        }
    }

    private fun changeItemState(item: ImageView, isSelected: Boolean) {
        if (isSelected) {
            item.setColorFilter(Color.WHITE)
            item.background =
                AppCompatResources.getDrawable(context, R.drawable.bg_selected_bottom_navigation)
        } else {
            item.setColorFilter(Color.BLACK)
            item.background = null
        }
    }

    fun getIndexItemSelected(callbacks: (int: Int) -> Unit) {
        callback = callbacks
    }
}

