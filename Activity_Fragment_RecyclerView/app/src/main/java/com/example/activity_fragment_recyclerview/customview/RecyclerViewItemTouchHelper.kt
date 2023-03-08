package com.example.activity_fragment_recyclerview.customview

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.activity_fragment_recyclerview.adapters.HomeRVAdapter

class RecyclerViewItemTouchHelper(dragDirs: Int, swipeDirs: Int) :
    SimpleCallback(dragDirs, swipeDirs) {
    private var callback: ((ViewHolder) -> Unit)? = null
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        callback?.let { it(viewHolder) }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foreground = (viewHolder as HomeRVAdapter.HomeViewHolder).view()
        getDefaultUIUtil().onDraw(
            c,
            recyclerView,
            foreground,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
        val foreground = (viewHolder as HomeRVAdapter.HomeViewHolder).view()
        getDefaultUIUtil().clearView(foreground)
    }

    fun setSwipedListener(callback: (ViewHolder) -> Unit) {
        this.callback = callback
    }
}