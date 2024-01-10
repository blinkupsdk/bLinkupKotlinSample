package com.blinkup.clientsampleapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blinkup.clientsampleapp.tabs.FragmentFriends
import com.blinkup.clientsampleapp.tabs.FragmentMapHolder
import com.blinkup.clientsampleapp.tabs.FragmentPresence
import com.blinkup.clientsampleapp.tabs.FragmentSettings

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentFriends()
            1 -> FragmentPresence()
            2 -> FragmentMapHolder()
            3 -> FragmentSettings()
            else -> throw IllegalStateException("Invalid position")
        }
    }

}