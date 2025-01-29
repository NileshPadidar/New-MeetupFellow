package com.connect.meetupsfellow.global.view

import android.graphics.Canvas
import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp


class SwipeToDeleteController : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var icon: Drawable? = null
    private var background: ColorDrawable? = null

    init {
        icon = ContextCompat.getDrawable(ArchitectureApp.instance!!.applicationContext, R.drawable.ic_baseline_delete_outline_24)
        background = ColorDrawable(Color.RED)
    }

    override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(
        canvas: Canvas, recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val iconBottom = iconTop + icon!!.intrinsicHeight

        when {
            dX > 0 -> { // Swiping to the right
                val iconLeft = itemView.left + iconMargin + icon!!.intrinsicWidth
                val iconRight = itemView.left + iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background!!.setBounds(
                    itemView.left, itemView.top,
                    (itemView.left + dX + backgroundCornerOffset).toInt(),
                    itemView.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background!!.setBounds(
                    (itemView.right + dX - backgroundCornerOffset).toInt(),
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> // view is unSwiped
                background!!.setBounds(0, 0, 0, 0)
        }

        background!!.draw(canvas)
        icon!!.draw(canvas)
    }
}