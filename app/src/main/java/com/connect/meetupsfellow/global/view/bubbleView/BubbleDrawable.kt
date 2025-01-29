package com.connect.meetupsfellow.global.view.bubbleView

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * Created by lgp on 2015/3/24.
 */
class BubbleDrawable private constructor(builder: Builder) : Drawable() {
    private val mRect: RectF?
    private val mPath = Path()
    private var mBitmapShader: BitmapShader? = null
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mArrowWidth: Float
    private val mAngle: Float
    private val mArrowHeight: Float
    private var mArrowPosition: Float = 0.toFloat()
    private val bubbleColor: Int
    private val bubbleBitmap: Bitmap?
    private val mArrowLocation: ArrowLocation
    private val bubbleType: BubbleType
    private val mArrowCenter: Boolean

    init {
        this.mRect = builder.mRect
        this.mAngle = builder.mAngle
        this.mArrowHeight = builder.mArrowHeight
        this.mArrowWidth = builder.mArrowWidth
        this.mArrowPosition = builder.mArrowPosition
        this.bubbleColor = builder.bubbleColor
        this.bubbleBitmap = builder.bubbleBitmap
        this.mArrowLocation = builder.mArrowLocation
        this.bubbleType = builder.bubbleType
        this.mArrowCenter = builder.arrowCenter
    }

    override fun draw(canvas: Canvas) {
        setUp(canvas)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    private fun setUpPath(mArrowLocation: ArrowLocation, path: Path) {
        when (mArrowLocation) {
            ArrowLocation.LEFT -> setUpLeftPath(mRect, path)
            ArrowLocation.RIGHT -> setUpRightPath(mRect, path)
            ArrowLocation.TOP -> setUpTopPath(mRect, path)
            ArrowLocation.BOTTOM -> setUpBottomPath(mRect, path)
        }
    }

    private fun setUp(canvas: Canvas) {
        when (bubbleType) {
            BubbleType.COLOR -> mPaint.color = bubbleColor
            BubbleType.BITMAP -> {
                if (bubbleBitmap == null)
                    return
                if (mBitmapShader == null) {
                    mBitmapShader = BitmapShader(bubbleBitmap,
                            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
                mPaint.shader = mBitmapShader
                setUpShaderMatrix()
            }
        }
        setUpPath(mArrowLocation, mPath)
        canvas.drawPath(mPath, mPaint)
    }

    private fun setUpLeftPath(rect: RectF?, path: Path) {

        if (mArrowCenter) {
            mArrowPosition = (rect!!.bottom - rect.top) / 2 - mArrowWidth / 2
        }

        path.moveTo(mArrowWidth + rect!!.left + mAngle, rect.top)
        path.lineTo(rect.width() - mAngle, rect.top)
        path.arcTo(RectF(rect.right - mAngle, rect.top, rect.right,
                mAngle + rect.top), 270f, 90f)
        path.lineTo(rect.right, rect.bottom - mAngle)
        path.arcTo(RectF(rect.right - mAngle, rect.bottom - mAngle,
                rect.right, rect.bottom), 0f, 90f)
        path.lineTo(rect.left + mArrowWidth + mAngle, rect.bottom)
        path.arcTo(RectF(rect.left + mArrowWidth, rect.bottom - mAngle,
                mAngle + rect.left + mArrowWidth, rect.bottom), 90f, 90f)
        path.lineTo(rect.left + mArrowWidth, mArrowHeight + mArrowPosition)
        path.lineTo(rect.left, mArrowPosition + mArrowHeight / 2)
        path.lineTo(rect.left + mArrowWidth, mArrowPosition)
        path.lineTo(rect.left + mArrowWidth, rect.top + mAngle)
        path.arcTo(RectF(rect.left + mArrowWidth, rect.top, mAngle
                + rect.left + mArrowWidth, mAngle + rect.top), 180f, 90f)
        path.close()
    }

    private fun setUpTopPath(rect: RectF?, path: Path) {

        if (mArrowCenter) {
            mArrowPosition = (rect!!.right - rect.left) / 2 - mArrowWidth / 2
        }

        path.moveTo(rect!!.left + Math.min(mArrowPosition, mAngle), rect.top + mArrowHeight)
        path.lineTo(rect.left + mArrowPosition, rect.top + mArrowHeight)
        path.lineTo(rect.left + mArrowWidth / 2 + mArrowPosition, rect.top)
        path.lineTo(rect.left + mArrowWidth + mArrowPosition, rect.top + mArrowHeight)
        path.lineTo(rect.right - mAngle, rect.top + mArrowHeight)

        path.arcTo(RectF(rect.right - mAngle,
                rect.top + mArrowHeight, rect.right, mAngle + rect.top + mArrowHeight), 270f, 90f)
        path.lineTo(rect.right, rect.bottom - mAngle)

        path.arcTo(RectF(rect.right - mAngle, rect.bottom - mAngle,
                rect.right, rect.bottom), 0f, 90f)
        path.lineTo(rect.left + mAngle, rect.bottom)

        path.arcTo(RectF(rect.left, rect.bottom - mAngle,
                mAngle + rect.left, rect.bottom), 90f, 90f)
        path.lineTo(rect.left, rect.top + mArrowHeight + mAngle)
        path.arcTo(RectF(rect.left, rect.top + mArrowHeight, mAngle + rect.left, mAngle + rect.top + mArrowHeight), 180f, 90f)
        path.close()
    }

    private fun setUpRightPath(rect: RectF?, path: Path) {

        if (mArrowCenter) {
            mArrowPosition = (rect!!.bottom - rect.top) / 2 - mArrowWidth / 2
        }

        path.moveTo(rect!!.left + mAngle, rect.top)
        path.lineTo(rect.width() - mAngle - mArrowWidth, rect.top)
        path.arcTo(RectF(rect.right - mAngle - mArrowWidth,
                rect.top, rect.right - mArrowWidth, mAngle + rect.top), 270f, 90f)
        path.lineTo(rect.right - mArrowWidth, mArrowPosition)
        path.lineTo(rect.right, mArrowPosition + mArrowHeight / 2)
        path.lineTo(rect.right - mArrowWidth, mArrowPosition + mArrowHeight)
        path.lineTo(rect.right - mArrowWidth, rect.bottom - mAngle)

        path.arcTo(RectF(rect.right - mAngle - mArrowWidth, rect.bottom - mAngle,
                rect.right - mArrowWidth, rect.bottom), 0f, 90f)
        path.lineTo(rect.left + mArrowWidth, rect.bottom)

        path.arcTo(RectF(rect.left, rect.bottom - mAngle,
                mAngle + rect.left, rect.bottom), 90f, 90f)

        path.arcTo(RectF(rect.left, rect.top, mAngle + rect.left, mAngle + rect.top), 180f, 90f)
        path.close()
    }

    private fun setUpBottomPath(rect: RectF?, path: Path) {
        if (mArrowCenter) {
            mArrowPosition = (rect!!.right - rect.left) / 2 - mArrowWidth / 2
        }
        path.moveTo(rect!!.left + mAngle, rect.top)
        path.lineTo(rect.width() - mAngle, rect.top)
        path.arcTo(RectF(rect.right - mAngle,
                rect.top, rect.right, mAngle + rect.top), 270f, 90f)

        path.lineTo(rect.right, rect.bottom - mArrowHeight - mAngle)
        path.arcTo(RectF(rect.right - mAngle, rect.bottom - mAngle - mArrowHeight,
                rect.right, rect.bottom - mArrowHeight), 0f, 90f)

        path.lineTo(rect.left + mArrowWidth + mArrowPosition, rect.bottom - mArrowHeight)
        path.lineTo(rect.left + mArrowPosition + mArrowWidth / 2, rect.bottom)
        path.lineTo(rect.left + mArrowPosition, rect.bottom - mArrowHeight)
        path.lineTo(rect.left + Math.min(mAngle, mArrowPosition), rect.bottom - mArrowHeight)

        path.arcTo(RectF(rect.left, rect.bottom - mAngle - mArrowHeight,
                mAngle + rect.left, rect.bottom - mArrowHeight), 90f, 90f)
        path.lineTo(rect.left, rect.top + mAngle)
        path.arcTo(RectF(rect.left, rect.top, mAngle + rect.left, mAngle + rect.top), 180f, 90f)
        path.close()
    }

    private fun setUpShaderMatrix() {
        val mShaderMatrix = Matrix()
        mShaderMatrix.set(null)
        val mBitmapWidth = bubbleBitmap!!.width
        val mBitmapHeight = bubbleBitmap.height
        val scaleX = intrinsicWidth / mBitmapWidth.toFloat()
        val scaleY = intrinsicHeight / mBitmapHeight.toFloat()
        mShaderMatrix.postScale(scaleX, scaleY)
        mShaderMatrix.postTranslate(mRect!!.left, mRect.top)
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    override fun getIntrinsicWidth(): Int {
        return mRect!!.width().toInt()
    }

    override fun getIntrinsicHeight(): Int {
        return mRect!!.height().toInt()
    }

    class Builder {
        internal var mRect: RectF? = null
        internal var mArrowWidth = DEFAULT_ARROW_WITH
        internal var mAngle = DEFAULT_ANGLE
        internal var mArrowHeight = DEFAULT_ARROW_HEIGHT
        internal var mArrowPosition = DEFAULT_ARROW_POSITION
        internal var bubbleColor = DEFAULT_BUBBLE_COLOR
        internal var bubbleBitmap: Bitmap? = null
        internal var bubbleType = BubbleType.COLOR
        internal var mArrowLocation = ArrowLocation.LEFT
        internal var arrowCenter: Boolean = false

        fun rect(rect: RectF): Builder {
            this.mRect = rect
            return this
        }

        fun arrowWidth(mArrowWidth: Float): Builder {
            this.mArrowWidth = mArrowWidth
            return this
        }

        fun angle(mAngle: Float): Builder {
            this.mAngle = mAngle * 2
            return this
        }

        fun arrowHeight(mArrowHeight: Float): Builder {
            this.mArrowHeight = mArrowHeight
            return this
        }

        fun arrowPosition(mArrowPosition: Float): Builder {
            this.mArrowPosition = mArrowPosition
            return this
        }

        fun bubbleColor(bubbleColor: Int): Builder {
            this.bubbleColor = bubbleColor
            bubbleType(BubbleType.COLOR)
            return this
        }

        fun bubbleBitmap(bubbleBitmap: Bitmap): Builder {
            this.bubbleBitmap = bubbleBitmap
            bubbleType(BubbleType.BITMAP)
            return this
        }

        fun arrowLocation(arrowLocation: ArrowLocation): Builder {
            this.mArrowLocation = arrowLocation
            return this
        }

        fun bubbleType(bubbleType: BubbleType): Builder {
            this.bubbleType = bubbleType
            return this
        }

        fun arrowCenter(arrowCenter: Boolean): Builder {
            this.arrowCenter = arrowCenter
            return this
        }

        fun build(): BubbleDrawable {
            if (mRect == null) {
                throw IllegalArgumentException("BubbleDrawable Rect can not be null")
            }
            return BubbleDrawable(this)
        }

        companion object {
            var DEFAULT_ARROW_WITH = 25f
            var DEFAULT_ARROW_HEIGHT = 25f
            var DEFAULT_ANGLE = 20f
            var DEFAULT_ARROW_POSITION = 50f
            var DEFAULT_BUBBLE_COLOR = Color.RED
        }
    }

    enum class ArrowLocation(val intValue: Int) {
        LEFT(0x00),
        RIGHT(0x01),
        TOP(0x02),
        BOTTOM(0x03);


        companion object {

            fun mapIntToValue(stateInt: Int): ArrowLocation {
                for (value in ArrowLocation.values()) {
                    if (stateInt == value.intValue) {
                        return value
                    }
                }
                return LEFT
            }
        }
    }

    enum class BubbleType {
        COLOR,
        BITMAP
    }
}
