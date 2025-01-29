package com.connect.meetupsfellow.global.view

import android.annotation.SuppressLint
import android.graphics.RectF
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import com.connect.meetupsfellow.global.interfaces.SwipeControllerActions


/**
 * Created by Jammwal on 27-02-2018.
 */

@Suppress("NAME_SHADOWING")
@SuppressLint("ClickableViewAccessibility")
class SwipeController(private val buttonsActions: SwipeControllerActions) : Callback() {

    override fun getMovementFlags(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
        val position = p1!!.adapterPosition
        val dragFlags = 0 // whatever your dragFlags need to be
        val swipeFlags = if ((position == 0) or (position == p0.adapter!!.itemCount - 1)) 0 else LEFT or RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
       return false
    }

    override fun onSwiped(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        when (p1) {
            ItemTouchHelper.LEFT -> {
                buttonsActions.onLeftClicked(p0.adapterPosition)
            }

            ItemTouchHelper.RIGHT -> {
                buttonsActions.onRightClicked(p0.adapterPosition)
            }
        }
    }

    private var swipeBack = false

    private var buttonShowedState = ButtonsState.GONE

    private var buttonInstance: RectF? = null

    private var currentItemViewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null

    private val buttonWidth = 300f

    internal enum class ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    /*  override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
          if (swipeBack) {
              swipeBack = buttonShowedState !== ButtonsState.GONE
              return 0
          }
          return super.convertToAbsoluteDirection(flags, layoutDirection)
      }

      override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
          var dX = dX
          if (actionState == ACTION_STATE_SWIPE) {
              if (buttonShowedState !== ButtonsState.GONE) {
                  if (buttonShowedState === ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth)
                  if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth)
                  super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
              } else {
                  setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
              }
          }

          if (buttonShowedState === ButtonsState.GONE) {
              super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
          }
          currentItemViewHolder = viewHolder
      }

      private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
          recyclerView.setOnTouchListener { v, event ->
              swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
              if (swipeBack) {
                  if (dX < -buttonWidth)
                      buttonShowedState = ButtonsState.RIGHT_VISIBLE
                  else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE

                  if (buttonShowedState !== ButtonsState.GONE) {
                      setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                      setItemsClickable(recyclerView, false)
                  }
              }
              false
          }
      }

      @SuppressLint("ClickableViewAccessibility")
      private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
          recyclerView.setOnTouchListener { v, event ->
              if (event.action == MotionEvent.ACTION_DOWN) {
                  setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
              }
              false
          }
      }


      private fun setTouchUpListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
          recyclerView.setOnTouchListener { v, event ->
              if (event.action == MotionEvent.ACTION_UP) {
                  super@SwipeController.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
                  recyclerView.setOnTouchListener { v, event -> false }
                  setItemsClickable(recyclerView, true)
                  swipeBack = false

                  if (buttonsActions != null && buttonInstance != null && buttonInstance!!.contains(event.x, event.y)) {
                      if (buttonShowedState === ButtonsState.LEFT_VISIBLE) {
                          buttonsActions.onLeftClicked(viewHolder.adapterPosition)
                      } else if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
                          buttonsActions.onRightClicked(viewHolder.adapterPosition)
                      }
                  }
                  buttonShowedState = ButtonsState.GONE
                  currentItemViewHolder = null
              }
              false
          }
      }

      private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
          for (i in 0 until recyclerView.childCount) {
              recyclerView.getChildAt(i).isClickable = isClickable
          }
      }

      private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
          val buttonWidthWithoutPadding = buttonWidth - 20
          val corners = 16f

          val itemView = viewHolder.itemView
          val p = Paint()

          val leftButton = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left + buttonWidthWithoutPadding, itemView.bottom.toFloat())
          p.setColor(Color.BLUE)
          c.drawRoundRect(leftButton, corners, corners, p)
          drawText("Edit", c, leftButton, p)

          val rightButton = RectF(itemView.right - buttonWidthWithoutPadding, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
          p.setColor(Color.RED)
          c.drawRoundRect(rightButton, corners, corners, p)
          drawText("DELETE", c, rightButton, p)

          buttonInstance = null
          if (buttonShowedState === ButtonsState.LEFT_VISIBLE) {
              buttonInstance = leftButton
          } else if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
              buttonInstance = rightButton
          }
      }

      private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
          val textSize = 60f
          p.color = Color.WHITE
          p.isAntiAlias = true
          p.textSize = textSize

          val textWidth = p.measureText(text)
          c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
      }

      fun onDraw(c: Canvas) {
          if (currentItemViewHolder != null) {
              drawButtons(c, currentItemViewHolder!!)
          }
      }*/
}