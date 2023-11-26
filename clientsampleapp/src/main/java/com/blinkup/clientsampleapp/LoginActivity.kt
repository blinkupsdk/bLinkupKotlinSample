package com.blinkup.clientsampleapp

import android.os.Bundle
import com.blinkup.clientsampleapp.base.BaseActivity

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentEnterPhone())
            .commit()
    }
}