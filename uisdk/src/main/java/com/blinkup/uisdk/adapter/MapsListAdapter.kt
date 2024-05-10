package com.blinkup.uisdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.uisdk.R
import com.blinkupapp.sdk.data.model.Place

class MapsListAdapter(data: List<Place>, private val onPlaceClick: (Place) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View, private val onPlaceClick: (Place) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val root = view

        fun bind(place: Place) {
            nameView.text = place.name
            root.setOnClickListener {
                onPlaceClick(place)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.maps_list_item, parent, false)
        return ViewHolder(view) { place ->
            onPlaceClick(place)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(data[position])
    }
}
