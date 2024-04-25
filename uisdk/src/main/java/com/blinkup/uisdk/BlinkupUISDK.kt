package com.blinkup.uisdk

import android.content.Context

class BlinkupUISDK {
    companion object {
        fun launch(context: Context, clientId: String) {
            context.startActivity(LoginActivity.createIntent(context, clientId))
        }
    }
}