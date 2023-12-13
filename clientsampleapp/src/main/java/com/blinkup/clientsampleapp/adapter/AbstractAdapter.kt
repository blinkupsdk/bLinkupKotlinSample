package com.blinkup.clientsampleapp.adapter

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractAdapter<T : RecyclerView.ViewHolder?>: RecyclerView.Adapter<T>() {
    lateinit var lifecycleOwner: LifecycleOwner
}