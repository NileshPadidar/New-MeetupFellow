package com.connect.meetupsfellow.mvp.view.viewholder

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemReasonBinding
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.retrofit.response.ResponseFlagReason

/*
class ReasonsViewHolder(
    private val binding: ItemReasonBinding,
    clickListener: RecyclerViewClickListener
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            clickListener.onClick(itemView, adapterPosition)
        }
    }

    lateinit var responseFlagReason: ResponseFlagReason

    internal fun bind(responseFlagReason: ResponseFlagReason) {
        this.responseFlagReason = responseFlagReason
        binding.tvFlagReason.text = responseFlagReason.reason_flag
        itemView.isSelected = responseFlagReason.selected
        binding.tvFlagReason.isSelected = responseFlagReason.selected
    }
}*/

class ReasonsViewHolder(
    private val binding: ItemReasonBinding,
    private val clickListener: RecyclerViewClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            clickListener.onClick(itemView, adapterPosition)
        }
    }

    internal fun bind(responseFlagReason: ResponseFlagReason) {
        binding.tvFlagReason.text = responseFlagReason.reason_flag

        // Apply gradient paint for selected items
        if (responseFlagReason.selected) {
            val paint = binding.tvFlagReason.paint
            val width = paint.measureText(binding.tvFlagReason.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, width, binding.tvFlagReason.textSize,
                intArrayOf(Color.parseColor("#F4447E"), Color.parseColor("#8448F4")),
                null, Shader.TileMode.REPEAT
            )
            binding.tvFlagReason.paint.shader = textShader
            binding.flagCheck.isChecked = true
            binding.flagLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
        } else {
            binding.tvFlagReason.paint.shader = null
            binding.flagCheck.isChecked = false
            binding.flagLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        }
    }

}

