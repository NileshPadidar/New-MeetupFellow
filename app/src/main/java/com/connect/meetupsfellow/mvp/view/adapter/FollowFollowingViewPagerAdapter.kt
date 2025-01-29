package com.connect.meetupsfellow.mvp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.connect.meetupsfellow.mvp.view.fragment.*

class FollowFollowingViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FollowerFragment()
            1 -> FollowingFragment()
            2 -> LikeUserListFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}