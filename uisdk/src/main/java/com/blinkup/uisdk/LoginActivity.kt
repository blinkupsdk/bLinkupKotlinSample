package com.blinkup.uisdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.blinkup.uisdk.base.BaseActivity
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.User

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        Blinkup.init(applicationContext)
        intent.getStringExtra(CLIENT_ID)?.let {
            clientId = it
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentEnterPhone())
            .commit()
    }

    companion object {
        const val CLIENT_ID = "CLIENT_ID"
        var user: User? = null
        var clientId: String? = null
    }
}