package com.blinkup.uisdk

import android.content.Context
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
        fun createIntent(context: Context, clientId: String): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                putExtra(CLIENT_ID, clientId)
            }
        }

        private const val CLIENT_ID = "CLIENT_ID"
        internal var user: User? = null
        internal var clientId: String? = null
    }
}