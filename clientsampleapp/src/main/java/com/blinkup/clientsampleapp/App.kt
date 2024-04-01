package com.blinkup.clientsampleapp

import android.app.Application
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.User

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        Blinkup.init(this)
    }

    companion object {
        var user: User? = null
    }

}