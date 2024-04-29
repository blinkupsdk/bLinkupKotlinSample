package com.blinkup.uisdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blinkup.uisdk.base.BaseActivity
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.User

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(CLIENT_ID)?.let {
            clientId = it
        }
        setTheme(intent.getIntExtra(THEME, R.style.DefaultTheme))
        logoId = intent.getIntExtra(LOGO_ID, 0)

        setContentView(R.layout.activity_basic)
        Blinkup.init(applicationContext)


        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentEnterPhone())
            .commit()
    }

    companion object {
        fun createIntent(
            context: Context,
            clientId: String,
            theme: Int = R.style.DefaultTheme,
            logoId: Int
        ): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                putExtra(CLIENT_ID, clientId)
                putExtra(THEME, theme)
                putExtra(LOGO_ID, logoId)
            }
        }


        private const val CLIENT_ID = "CLIENT_ID"
        private const val THEME = "THEME"
        private const val LOGO_ID = "LOGO_ID"
        internal var user: User? = null
        internal var clientId: String? = null
        internal var logoId: Int = 0
    }
}