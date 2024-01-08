package com.blinkup.clientsampleapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.blinkup.clientsampleapp.adapter.ViewPagerAdapter
import com.blinkup.clientsampleapp.base.BaseActivity
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkup.clientsampleapp.utils.findCurrentFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity() {
    val adapter = ViewPagerAdapter(this)
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("channelId", "Notification test", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Blinkup"
            }
            val nManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(notificationChannel)
        }
//        createNotificationChannel(notificationChannel)

        viewPager = findViewById<ViewPager2>(R.id.view_pager)
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

//    private fun createNotificationChannel(notificationChannel: NotificationChannel) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is not in the Support Library.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel("channelId", name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system.
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//
//        }
//    }
}