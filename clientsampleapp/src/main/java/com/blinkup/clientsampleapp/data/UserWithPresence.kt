package com.blinkup.clientsampleapp.data

import com.blinkupapp.sdk.data.model.User

data class UserWithPresence(val user: User?, val isPresent: Boolean)