package com.connect.meetupsfellow.global.utils

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.connect.meetupsfellow.R

class ToolbarUtils {
    companion object {
        fun setupActionBar(
            context: Context,
            toolbar: androidx.appcompat.widget.Toolbar,
            title: String,
            tvTitle: TextView,
            showBackArrow: Boolean,
            activity: AppCompatActivity
        ) {
            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(showBackArrow)
                setHomeAsUpIndicator(R.drawable.back_arrow)
                this.title = title
            }
            // Apply gradient to title text
            tvTitle.text = title
            val paint = tvTitle.paint
            val width = paint.measureText(tvTitle.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, width, tvTitle.textSize,
                intArrayOf(
                    Color.parseColor("#F4447E"),
                    Color.parseColor("#8448F4")
                ),
                null,
                Shader.TileMode.REPEAT
            )
            tvTitle.paint.shader = textShader
        }

    }
}
