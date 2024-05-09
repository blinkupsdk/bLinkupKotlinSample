package com.blinkup.uisdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.blinkup.uisdk.base.BaseActivity
import com.blinkup.uisdk.base.BaseFragment
import com.blinkup.uisdk.tabs.FragmentFriends
import com.blinkup.uisdk.utils.findCurrentFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(intent.getIntExtra(THEME, R.style.DefaultTheme))
        setContentView(R.layout.activity_sdk_ui_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentFriends())
            .commit()
    }

//    override fun onBackPressed() {
//        val fragment = viewPager.findCurrentFragment(fragmentManager = supportFragmentManager)
//        if ((fragment as BaseFragment?)?.onBackPressed() != true) {
//            if (viewPager.currentItem != 0) {
//                viewPager.currentItem = 0
//            } else {
//                super.onBackPressed()
//            }
//        }
//    }

    companion object {
        private const val THEME: String = "THEME"

        fun createIntent(context: Context, theme: Int): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(THEME, theme)
            }

        }
    }
}

interface OnPresenceUpdated {
    fun onPresenceUpdated()
}