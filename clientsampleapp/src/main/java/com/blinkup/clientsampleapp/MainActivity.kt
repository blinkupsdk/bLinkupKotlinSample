package com.blinkup.clientsampleapp

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.blinkup.clientsampleapp.adapter.ViewPagerAdapter
import com.blinkup.clientsampleapp.base.BaseActivity
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkup.clientsampleapp.tabs.FragmentFriends
import com.blinkup.clientsampleapp.utils.findCurrentFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity(), OnPresenceUpdated {
    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 1
        viewPager.adapter = ViewPagerAdapter(this)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.friends)
                    tab.text = "Friends"
                }

                1 -> {
                    tab.setIcon(R.drawable.presence)
                    tab.text = "Presence"
                }

                2 -> {
                    tab.setIcon(R.drawable.map)
                    tab.text = "Map"
                }

                3 -> {
                    tab.setIcon(R.drawable.settings)
                    tab.text = "Settings"
                }
            }
        }.attach()
    }

    override fun onBackPressed() {
        val fragment = viewPager.findCurrentFragment(fragmentManager = supportFragmentManager)
        if ((fragment as BaseFragment?)?.onBackPressed() != true) {
            if (viewPager.currentItem != 0) {
                viewPager.currentItem = 0
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onPresenceUpdated() {
        supportFragmentManager.fragments.find { it is FragmentFriends }?.let {
            (it as FragmentFriends).updatePresence()
        }
    }
}

interface OnPresenceUpdated {
    fun onPresenceUpdated()
}