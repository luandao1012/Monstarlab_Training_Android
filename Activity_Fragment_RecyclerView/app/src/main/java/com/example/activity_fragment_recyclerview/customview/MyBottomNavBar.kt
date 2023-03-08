package com.example.activity_fragment_recyclerview.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.example.activity_fragment_recyclerview.R
import com.example.activity_fragment_recyclerview.databinding.MyBottomNavBarBinding

class MyBottomNavBar @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    LinearLayout(context, attr) {
    private val binding: MyBottomNavBarBinding
    private var indexSelectedItem: Int = 0
    private var callback: ((int: Int) -> Unit)? = null

    init {
        binding = MyBottomNavBarBinding.inflate(LayoutInflater.from(context), this, true)
        binding.llBottomNavBar.children.forEachIndexed { index, view ->
            if (index == indexSelectedItem) changeItemState(view as ImageView, true)
            view.setOnClickListener { onItemClick(index, view as ImageView) }
        }
    }

    private fun onItemClick(index: Int, view: ImageView) {
        if (index != indexSelectedItem) {
            callback?.invoke(index)
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

    fun setFragmentSelected(callbacks: (int: Int) -> Unit) {
        callback = callbacks
    }

    fun setIndexSelected(index: Int) {
        indexSelectedItem = index
        binding.llBottomNavBar.children.forEachIndexed { i, view ->
            if (i == indexSelectedItem) {
                changeItemState(view as ImageView, true)
            } else {
                changeItemState(view as ImageView, false)
            }
        }
    }
}

