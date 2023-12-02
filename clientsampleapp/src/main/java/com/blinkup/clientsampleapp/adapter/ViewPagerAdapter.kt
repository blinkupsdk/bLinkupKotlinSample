package com.blinkup.clientsampleapp.adapter

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blinkup.clientsampleapp.tabs.FragmentFriends
import com.blinkup.clientsampleapp.tabs.FragmentMap
import com.blinkup.clientsampleapp.tabs.FragmentPresence
import com.blinkup.clientsampleapp.tabs.FragmentSettings

class ViewPagerAdapter(fragmentActivity: FragmentActivity, var context: Context) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentFriends(context)
            1 -> FragmentPresence()
            2 -> FragmentMap()
            3 -> FragmentSettings()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}