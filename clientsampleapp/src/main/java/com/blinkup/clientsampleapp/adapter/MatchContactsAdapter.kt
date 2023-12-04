package com.blinkup.clientsampleapp.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkup.clientsampleapp.R


class MatchContactsAdapter(var data: List<ContactResult>) : RecyclerView.Adapter<MatchContactsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val contactView: TextView
        private val sendRequest: Button

        init{
            contactView = view.findViewById(R.id.contac)
        }

        fun bind() {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = La
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}