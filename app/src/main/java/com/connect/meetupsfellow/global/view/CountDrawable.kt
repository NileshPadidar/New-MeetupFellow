package com.connect.meetupsfellow.global.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.R


class CountDrawable(context: Context) : Drawable() {

    override fun setColorFilter(p0: ColorFilter?) {

    }

    private var mBadgePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private val mTxtRect = Rect()

    private var mCount = ""
    private var mWillDraw = false

    init {
        val mTextSize = context.resources.getDimensionPixelSize(R.dimen.text_size_button_xxxxs) / 1.1

        if (null == mBadgePaint)
            mBadgePaint = Paint()
        mBadgePaint!!.color = ContextCompat.getColor(context.applicationContext, R.color.colorRed)
        mBadgePaint!!.isAntiAlias = true
        mBadgePaint!!.style = Paint.Style.FILL

        if (null == mTextPaint)
            mTextPaint = Paint()
        mTextPaint!!.color = Color.WHITE
        mTextPaint!!.typeface = Typeface.DEFAULT
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (!mWillDraw) {
            return
        }
        val bounds = bounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top

        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */

        val radius = Math.max(width, height) / 2 / 2
        val centerX = width - radius - 1f + 5
        val centerY = radius - 5
        if (mCount.length <= 2) {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY.toFloat(), (radius + 5.5).toFloat(), mBadgePaint!!)
        } else {
            canvas.drawCircle(centerX, centerY.toFloat(), (radius + 6.5).toFloat(), mBadgePaint!!)
        }
        // Draw badge count text inside the circle.
        mTextPaint!!.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = mTxtRect.bottom - mTxtRect.top
        val textY = centerY + textHeight / 2f
        if (mCount.length > 2)
            canvas.drawText("99+", centerX, textY, mTextPaint!!)
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint!!)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    internal fun setCount(count: String) {
        mCount = count

        // Only draw a badge if there are notifications.
        mWillDraw = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // Do Nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

}