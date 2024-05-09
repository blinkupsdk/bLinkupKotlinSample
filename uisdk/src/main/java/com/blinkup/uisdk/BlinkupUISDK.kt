package com.blinkup.uisdk

import android.content.Context

class BlinkupUISDK {
    companion object {

        fun launch(
            context: Context,
            clientId: String,
            customerName: String,
            theme: Int = R.style.DefaultTheme,
            logoId: Int = 0,
        ) {
            context.startActivity(
                LoginActivity.createIntent(
                    context,
                    clientId,
                    theme,
                    logoId,
                    customerName
                )
            )
        }
    }
}