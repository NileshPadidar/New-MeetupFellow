package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.global.interfaces.ItemClick

/**
 * Created by Jammwal on 28-02-2018.
 */
object RecyclerViewClick {

    private var click: ItemClick? = null

    internal fun enableClick(click: ItemClick) {
        this.click = click
    }

    internal fun disableClick() {
        click = null
    }

    internal val onClickListener: ItemClick?
        get() = click
}