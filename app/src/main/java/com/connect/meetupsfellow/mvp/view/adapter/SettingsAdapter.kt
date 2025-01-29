package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.databinding.ItemSearchBinding
import com.connect.meetupsfellow.databinding.ItemSettingsBinding
import com.connect.meetupsfellow.mvp.view.model.SettingsModel
import com.connect.meetupsfellow.mvp.view.viewholder.SearchItemViewHolder
import com.connect.meetupsfellow.mvp.view.viewholder.SettingsViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseSettings

class SettingsAdapter : RecyclerView.Adapter<SettingsViewHolder>() {

    private val settings = arrayListOf<SettingsModel>()
    private val settingsName = ArchitectureApp.instance!!.resources.getStringArray(R.array.settings)
    private val icons = intArrayOf(
        R.drawable.login_mobile,
        R.drawable.ic_baseline_notifications_none_24,
        R.drawable.calendar,
        R.drawable.calendar,
        R.drawable.ic_baseline_filter_alt_24,
        R.drawable.ic_private_album,
        R.drawable.ic_baseline_person_remove_24,
        R.drawable.ic_baseline_delete_forever_24,
        R.drawable.ic_unit_measure

    )

    internal fun getSettings(setting: ResponseSettings) {
        if (settings.isNotEmpty())
            settings.clear()
        setupSettings(settingsName[0], false, icons[0])
        setupSettings(settingsName[1], setting.allowPush != 0, icons[1])
        setupSettings(settingsName[2], setting.allowEvent != 0, icons[2])
        setupSettings(settingsName[3], setting.allowEventUpdate != 0, icons[3])
        setupSettings(settingsName[4], setting.nsfw != 0, icons[4])
        setupSettings(settingsName[5], false, icons[5])
        setupSettings(settingsName[6], false, icons[6])
        setupSettings(settingsName[7], false, icons[7])
        setupSettings(settingsName[8], setting.unit != 0, icons[8])
//        setupSettings(settingsName[6], false, icons[5])
        notifyDataSetChanged()
    }

    private fun setupSettings(name: String, selected: Boolean, icon: Int) {

        settings.add(SettingsModel().apply {
            this.name = name
            this.selected = selected
            this.icon = icon
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
       /* return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_settings,
                parent,
                false
            )
        )*/
        val binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SettingsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(viewHolder: SettingsViewHolder, position: Int) {
        viewHolder.bind(settings[viewHolder.adapterPosition])
    }
}