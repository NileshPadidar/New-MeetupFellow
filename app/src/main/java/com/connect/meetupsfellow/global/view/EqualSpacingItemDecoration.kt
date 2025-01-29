package com.connect.meetupsfellow.global.view

import android.graphics.Rect
import android.view.View


/**
 * Created by Jammwal on 19-02-2018.
 */
class EqualSpacingItemDecoration(private val spacing: Int, private var displayMode: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    companion object {
        const val DEFAULT_IMAGE_SIZE = 300
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        const val GRID = 2
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager!!, position, itemCount)
    }

    private fun setSpacingForDirection(outRect: Rect,
                                       layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager,
                                       position: Int,
                                       itemCount: Int) {

        // Resolve display mode automatically
        if (displayMode == -1) {
            displayMode = resolveDisplayMode(layoutManager)
        }

        when (displayMode) {
            HORIZONTAL -> {
                outRect.left = spacing
                outRect.right = if (position == itemCount - 1) spacing else 0
                outRect.top = spacing
                outRect.bottom = spacing
            }
            VERTICAL -> {
                outRect.left = spacing
                outRect.right = spacing
                outRect.top = spacing
                outRect.bottom = if (position == itemCount - 1) spacing else 0
            }
            GRID -> if (layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
                val cols = layoutManager.spanCount
                val rows = itemCount / cols

                outRect.left = spacing
                outRect.right = if (position % cols == cols - 1) spacing else 0
                outRect.top = spacing
                outRect.bottom = if (position / cols == rows - 1) spacing else 0
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager): Int {
        if (layoutManager is androidx.recyclerview.widget.GridLayoutManager) return GRID
        return if (layoutManager.canScrollHorizontally()) HORIZONTAL else VERTICAL
    }
}