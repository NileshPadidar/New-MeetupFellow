package com.connect.meetupsfellow.global.view

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.View

/**
 * Created by Jammwal on 28-02-2018.
 */
class ExtendedViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return (v as? TouchImageView)?.canScrollHorizontallyFroyo(-dx)
                ?: super.canScroll(v, checkV, dx, x, y)
    }

}