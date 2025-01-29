package com.connect.meetupsfellow.mvp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.connect.meetupsfellow.mvp.view.fragment.GetConnectionRequestFragment
import com.connect.meetupsfellow.mvp.view.fragment.MyConnectionsFragment
import com.connect.meetupsfellow.mvp.view.fragment.SendConnectionRequestFragment

class ConnectionViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> MyConnectionsFragment()
            1 -> GetConnectionRequestFragment()
            2 -> SendConnectionRequestFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
