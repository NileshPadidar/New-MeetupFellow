package com.connect.meetupsfellow.global.view

import android.content.Context
import androidx.core.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


@Suppress("DEPRECATION")
/**
 * Created by Jammwal on 17-02-2018.
 */
class RecyclerTouchListener(context: Context, recyclerView: androidx.recyclerview.widget.RecyclerView,
                            private val onTouchActionListener: OnTouchActionListener
) : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
    override fun onTouchEvent(p0: androidx.recyclerview.widget.RecyclerView, p1: MotionEvent) {
    }

    override fun onInterceptTouchEvent(p0: androidx.recyclerview.widget.RecyclerView, p1: MotionEvent): Boolean {
        return  mGestureDetector!!.onTouchEvent(p1)
    }

    private val gestureDetector: GestureDetector.SimpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val child = recyclerView.findChildViewUnder(e!!.x, e.y)
            if (child != null) {
                val childPosition = recyclerView.getChildPosition(child)
                onTouchActionListener.onClick(child, childPosition)
            }
            return false
        }
    }

    private var mGestureDetector: GestureDetectorCompat? = null

    init {
        mGestureDetector = GestureDetectorCompat(context, gestureDetector)
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    interface OnTouchActionListener {
        fun onClick(view: View, position: Int)
    }
}