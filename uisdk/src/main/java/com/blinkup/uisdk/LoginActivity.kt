package com.blinkup.uisdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.lifecycleScope
import com.blinkup.uisdk.base.BaseActivity
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(CLIENT_ID)?.let {
            clientId = it
        }
        LoginActivity.theme = intent.getIntExtra(THEME, R.style.DefaultTheme)
        setTheme(LoginActivity.theme)
        logoId = intent.getIntExtra(LOGO_ID, 0)
        customerName = intent.getStringExtra(CUSTOMER_NAME) ?: ""

        setContentView(R.layout.activity_basic)
        Blinkup.init(applicationContext)


        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Connect" // Set the title of the ActionBar

        if (Blinkup.isLoginRequired()) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentEnterPhone())
                .commit()
        } else {
            showLoading()
            lifecycleScope.launch {
                try {
                    user = Blinkup.checkSessionAndLogin()
                    openMainActivity()
                } catch (e: Exception) {
                    showErrorMessage(e.message ?: "Something went wrong")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    private fun openMainActivity() {

        startActivity(MainActivity.createIntent(this, LoginActivity.theme))
        finish()
    }


    companion object {
        fun createIntent(
            context: Context,
            clientId: String,
            theme: Int = R.style.DefaultTheme,
            logoId: Int,
            customerName: String
        ): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                putExtra(CLIENT_ID, clientId)
                putExtra(THEME, theme)
                putExtra(LOGO_ID, logoId)
                putExtra(CUSTOMER_NAME, customerName)
            }
        }


        private const val CLIENT_ID = "CLIENT_ID"
        private const val THEME = "THEME"
        private const val LOGO_ID = "LOGO_ID"
        private const val CUSTOMER_NAME: String = "CUSTOMER_NAME"
        internal var user: User? = null
        internal var clientId: String? = null
        internal var logoId: Int = 0
        internal var theme: Int = R.style.DefaultTheme
        internal var customerName: String = ""
    }
}