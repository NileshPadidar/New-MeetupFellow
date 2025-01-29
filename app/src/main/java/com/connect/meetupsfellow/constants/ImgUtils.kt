package com.connect.meetupsfellow.constants

import android.annotation.SuppressLint
import android.content.ClipData
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.LinearLayout

object ImgUtils {
    private var lastTime: Long = 0
    @SuppressLint("ClickableViewAccessibility")
    var MyTouchEvent = OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            // LogUtils.loge("ACTION_DOWN");
            val lastTime1 = System.currentTimeMillis()
            lastTime = lastTime1
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = DragShadowBuilder(v)
            v.startDrag(data, shadowBuilder, v, 0)
            if (lastTime1 - lastTime < 200) {
                v.alpha = 1.0.toFloat()
                return@OnTouchListener false
            }
            v.alpha = 0.5.toFloat()
        } else if (event.action == MotionEvent.ACTION_UP) {
            // LogUtils.loge("ACTION_UP");
            val time = System.currentTimeMillis()
            if (time - lastTime < 500) { // The interval is less than 500 milliseconds, considered as a click event
                return@OnTouchListener false
            }
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            //  LogUtils.loge("ACTION_MOVE");
        }
        false
    }
    var MyDragListener = OnDragListener { v, event ->
        val visitorView = event.localState as View ?: return@OnDragListener true
        val visitorOwner = visitorView.parent as ViewGroup
        val visitedOwner = v as ViewGroup
        val visitedImage = visitedOwner.getChildAt(0)
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> v.setAlpha(0.7.toFloat())
            DragEvent.ACTION_DROP -> {
                visitedImage.alpha = 1.0.toFloat()
                if (visitorOwner !== visitedOwner) {
                    visitedOwner.removeView(visitedImage)
                    visitorOwner.removeView(visitorView)
                    visitorOwner.addView(visitedImage)
                    val container = v as LinearLayout
                    container.addView(visitorView)
                } else {
                    visitedImage.performClick() // If return to the reset position, respond to click event
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                v.setAlpha(1.0.toFloat())
                visitedImage.alpha = 1.0.toFloat()
            }
            DragEvent.ACTION_DRAG_EXITED -> v.setAlpha(1.0.toFloat())
        }
        true
    }
}