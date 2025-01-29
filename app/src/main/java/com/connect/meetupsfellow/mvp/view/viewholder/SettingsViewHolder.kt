package com.connect.meetupsfellow.mvp.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemSettingsBinding
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.mvp.view.model.SettingsModel

class SettingsViewHolder(private val binding: ItemSettingsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.scSelection.setOnClickListener {
            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(
                adapterPosition, ItemClickStatus.Settings
            )
        }
        itemView.setOnClickListener {
            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(adapterPosition, ItemClickStatus.Number)
        }

        binding.tvImperial.setOnClickListener {
//            itemView.tvImperial.isSelected = true
//            itemView.tvMetric.isSelected = false

            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(
                adapterPosition, ItemClickStatus.SettingsImperial
            )
        }

        binding.tvMetric.setOnClickListener {
//            itemView.tvImperial.isSelected = false
//            itemView.tvMetric.isSelected = true

            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener
            RecyclerViewClick.onClickListener!!.onItemClick(
                adapterPosition, ItemClickStatus.SettingsMetric
            )
        }


    }

    internal fun bind(settingsModel: SettingsModel) {
        binding.tvSelection.text = settingsModel.name
        binding.scSelection.isChecked = settingsModel.selected
        binding.ivIcon.setImageResource(settingsModel.icon)
        when (adapterPosition) {
            0, 5, 6, 7 -> {
                binding.scSelection.visibility = View.GONE
                binding.clUnit.visibility = View.GONE
            }

            8 -> {
                binding.scSelection.visibility = View.GONE
                binding.clUnit.visibility = View.VISIBLE

                if (settingsModel.selected) {
                    binding.tvImperial.isSelected = true
                    binding.tvMetric.isSelected = false
                } else {
                    binding.tvImperial.isSelected = false
                    binding.tvMetric.isSelected = true
                }

            }

            else -> {
                binding.scSelection.visibility = View.VISIBLE
                binding.clUnit.visibility = View.GONE
            }
        }
    }
}