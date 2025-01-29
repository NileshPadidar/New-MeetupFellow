package com.connect.meetupsfellow.mvp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabLayoutAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    private val fragment = arrayListOf<Fragment>()
    private val title = arrayListOf<CharSequence>()

    internal fun addFragment(fragment: Fragment, title: CharSequence) {
        this.fragment.add(fragment)
        this.title.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragment[position]
    }

    override fun getCount(): Int {
        return fragment.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }

}