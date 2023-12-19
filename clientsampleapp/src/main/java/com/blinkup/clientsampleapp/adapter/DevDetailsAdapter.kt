package com.blinkup.clientsampleapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.data.model.Place

class DevDetailsAdapter(var data: List<Place>) :AbstractAdapter<DevDetailsAdapter.MyViewHolder>() {

//    private val presence = data.map { Presence(place = it, isPresent = Blinkup.isUserAtEvent(it)) }

    class MyViewHolder(val view: View, val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(view) {

        private val eventName: TextView
        private val eventLat: TextView
        private val eventLong: TextView
        private val eventRadius: TextView

        init {
            eventName = view.findViewById(R.id.event_name)
            eventLat = view.findViewById(R.id.lat_coord)
            eventLong = view.findViewById(R.id.long_coord)
            eventRadius = view.findViewById(R.id.radius)
        }

        fun bind(event: Place) {

            Log.i("events", "event long: ${event.longitude}")
            eventName.text = event.name
            eventLat.text = event.latitude.toString()
            eventLong.text = event.longitude.toString()
            eventRadius.text = event.radius.toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dev_list_item, parent, false)
        return MyViewHolder(view, lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

}