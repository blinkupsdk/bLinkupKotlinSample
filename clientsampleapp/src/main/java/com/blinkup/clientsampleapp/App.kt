package com.blinkup.clientsampleapp

import android.app.Application
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.User

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        Blinkup.init("Ph_yH2e8JRpc0WBKiNNOYUYJs03kNEY3DXh7WIrXlJo=", this)
    }

    companion object {
        var user: User? = null
    }

}