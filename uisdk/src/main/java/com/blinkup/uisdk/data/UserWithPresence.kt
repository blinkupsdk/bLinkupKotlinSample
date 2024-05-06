package com.blinkup.uisdk.data

import com.blinkupapp.sdk.data.model.Connection
import com.blinkupapp.sdk.data.model.User

data class UserWithPresence(val user: User?, val isPresent: Boolean,val connection: Connection?)