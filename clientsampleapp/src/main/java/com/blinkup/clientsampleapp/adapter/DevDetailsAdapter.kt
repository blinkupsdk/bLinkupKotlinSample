package com.blinkup.clientsampleapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.data.model.Place
import com.blinkupapp.sdk.data.model.Presence

class DevDetailsAdapter(var data: List<Place>, var event: List<Presence>) :AbstractAdapter<DevDetailsAdapter.MyViewHolder>() {

//    private val presence = data.map { Presence(place = it, isPresent = Blinkup.isUserAtEvent(it)) }

    class MyViewHolder(val view: View, val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(view) {

        private val eventName: TextView= view.findViewById(R.id.event_name)
        private val eventLat: TextView= view.findViewById(R.id.lat_coord)
        private val eventLong: TextView= view.findViewById(R.id.long_coord)
        private val eventRadius: TextView= view.findViewById(R.id.radius)
        private val nameView: TextView = view.findViewById(R.id.event_name)
        private val nameUnderlined: TextView = view.findViewById(R.id.nameUnderlined)
        private val isHere: ImageView = view.findViewById(R.id.isHere)

        fun bind(event: Place, presence: Presence, viewType: ViewType) {

            Log.i("events", "event long: ${event.longitude}")
            eventName.text = event.name
            eventLat.text = event.latitude.toString()
            eventLong.text = event.longitude.toString()
            eventRadius.text = event.radius.toString()
            nameView.text = presence.place?.name
            nameUnderlined.text = presence.place?.name
            isHere.visibility = if (presence.isPresent == true) View.VISIBLE else View.GONE
            nameUnderlined.visibility = if (presence.isPresent == true) View.VISIBLE else View.GONE
            nameView.visibility = if (presence.isPresent == true) View.GONE else View.VISIBLE

            val root = view


            when (viewType) {
                ViewType.TOP -> {

                } else -> {
                root.setBackgroundResource(R.drawable.top_border)
                }
            }

        }
    }

    enum class ViewType {
        TOP,
        OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dev_list_item, parent, false)
        return MyViewHolder(view, lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position], event[position], when (position) {
            0 -> ViewType.TOP
            else -> {ViewType.OTHER}
        })
    }
}