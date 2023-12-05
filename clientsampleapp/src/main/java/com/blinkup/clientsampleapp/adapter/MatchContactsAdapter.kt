package com.blinkup.clientsampleapp.adapter

import android.util.Log
import android.view.LayoutInflater
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
import com.blinkupapp.sdk.data.model.Contact


class MatchContactsAdapter(var data: List<ContactResult>) : RecyclerView.Adapter<MatchContactsAdapter.MyViewHolder>() {


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {



        private val contactName: TextView
        private val sendRequest: Button

        init{
            contactName = view.findViewById(R.id.name)
            sendRequest = view.findViewById(R.id.send_request_button)
        }

        fun bind(contact: ContactResult) {

            Log.i("contacts", "contacts adapter: $contact")

            contactName.text = contact.name

            sendRequest.setOnClickListener {
                //TODO
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        Log.i("contacts", "onCreateViewHolder")
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i("contacts", "onBindViewHolder: $data")
        holder.bind(data[position])
    }

}