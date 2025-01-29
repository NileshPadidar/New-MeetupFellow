package com.connect.meetupsfellow.global.interfaces

import com.connect.meetupsfellow.constants.ItemClickStatus

/**
 * Created by Jammwal on 28-02-2018.
 */
interface ItemClick {
    fun onItemClick(position: Int, status: ItemClickStatus)
}