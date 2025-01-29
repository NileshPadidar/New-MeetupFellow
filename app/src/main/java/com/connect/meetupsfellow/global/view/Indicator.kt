package com.connect.meetupsfellow.global.view

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import java.util.*

/**
 * Created by Maheshwar on 2016/8/5.
 */

@Suppress("SENSELESS_COMPARISON")
abstract class Indicator : Drawable(), Animatable {

    private val mUpdateListeners = HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>()

    private var mAnimators: ArrayList<ValueAnimator>? = null
    private var alpha = 255
    private var myDrawBounds = ZERO_BOUNDS_RECT

    private var mHasAnimators: Boolean = false

    private val mPaint = Paint()

    init {
        mPaint.color = ContextCompat.getColor(ArchitectureApp.instance!!, R.color.colorAccent)
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    var color: Int
        get() = mPaint.color
        set(color) {
            mPaint.color = color
        }

    override fun setAlpha(alpha: Int) {
        this.alpha = alpha
    }

    override fun getAlpha(): Int = alpha

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun draw(canvas: Canvas) {
        draw(canvas, mPaint)
    }

    abstract fun draw(canvas: Canvas, paint: Paint)

    abstract fun onCreateAnimators(): ArrayList<ValueAnimator>

    override fun start() {
        ensureAnimators()

        if (mAnimators == null) {
            return
        }

        // If the animators has not ended, do nothing.
        if (isStarted) {
            return
        }
        startAnimators()
        invalidateSelf()
    }

    private fun startAnimators() {
        for (i in mAnimators!!.indices) {
            val animator = mAnimators!![i]

            //when the animator restart , add the updateListener again because they
            // was removed by animator stop .
            val updateListener = mUpdateListeners[animator]
            if (updateListener != null) {
                animator.addUpdateListener(updateListener)
            }

            animator.start()
        }
    }

    private fun stopAnimators() {
        if (mAnimators != null) {
            for (animator in mAnimators!!) {
                if (animator != null && animator.isStarted) {
                    animator.removeAllUpdateListeners()
                    animator.end()
                }
            }
        }
    }

    private fun ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    override fun stop() {
        stopAnimators()
    }

    private val isStarted: Boolean
        get() {
            return mAnimators!!
                    .firstOrNull()
                    ?.isStarted
                    ?: false
        }

    override fun isRunning(): Boolean {
        return mAnimators!!
                .firstOrNull()
                ?.isRunning
                ?: false
    }

    /**
     * Your should use this to add AnimatorUpdateListener when
     * create animator , otherwise , animator doesn't work when
     * the animation restart .
     *
     * @param updateListener
     */
    fun addUpdateListener(animator: ValueAnimator, updateListener: ValueAnimator.AnimatorUpdateListener) {
        mUpdateListeners.put(animator, updateListener)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        setDrawBounds(bounds)
    }

    private fun setDrawBounds(drawBounds: Rect) {
        setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom)
    }

    private fun setDrawBounds(left: Int, top: Int, right: Int, bottom: Int) {
        this.myDrawBounds = Rect(left, top, right, bottom)
    }

    fun postInvalidate() {
        invalidateSelf()
    }

    fun getDrawBounds(): Rect = myDrawBounds

    val width: Int
        get() = myDrawBounds.width()

    val height: Int
        get() = myDrawBounds.height()

    fun centerX(): Int = myDrawBounds.centerX()

    fun centerY(): Int = myDrawBounds.centerY()

    fun exactCenterX(): Float = myDrawBounds.exactCenterX()

    fun exactCenterY(): Float = myDrawBounds.exactCenterY()

    companion object {
        private val ZERO_BOUNDS_RECT = Rect()
    }

}
