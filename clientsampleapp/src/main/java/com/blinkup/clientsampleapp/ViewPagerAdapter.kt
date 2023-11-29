package com.blinkup.clientsampleapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentFriends()
            1 -> FragmentPresence()
            2 -> FragmentMap()
            3 -> FragmentSettings()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}