package com.blinkup.clientsampleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.data.model.ContactResult

class PendingRequestAdapter(var data: List<ContactResult>): RecyclerView.Adapter<PendingRequestAdapter.MyViewHolder>() {

    private var phoneContacts = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {



        private val contactName: TextView
        private val userId: TextView
        private val sendRequest: Button

        init{
            contactName = view.findViewById(R.id.name)
            userId = view.findViewById(R.id.user_id)
            sendRequest = view.findViewById(R.id.send_request_button)
        }

        fun bind(contact: ContactResult) {

            contactName.text = contact.name
            userId.text = contact.userId

            sendRequest.setOnClickListener {
                //TODO
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return phoneContacts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(phoneContacts[position])
    }


}