package com.blinkup.clientsampleapp

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.blinkup.clientsampleapp.adapter.ViewPagerAdapter
import com.blinkup.clientsampleapp.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
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
}