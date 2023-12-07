package com.blinkup.clientsampleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.Blinkup.Companion.sendFriendRequest
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkupapp.sdk.data.model.User

class PendingRequestAdapter(var data: List<ContactResult>): RecyclerView.Adapter<PendingRequestAdapter.MyViewHolder>() {
    private var phoneContacts = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val contactName: TextView
        private val userId: TextView
        val sendRequest: Button = view.findViewById(R.id.send_request_button)

        init{
            contactName = view.findViewById(R.id.name)
            userId = view.findViewById(R.id.user_id)
        }

        fun bind(phoneContact: ContactResult) {

            contactName.text = phoneContact.name
            userId.text = phoneContact.userId

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
        holder.sendRequest.setOnClickListener {
            //TODO Find out how to turn a ContactResult into a User so it can be used in sendFriendRequest
//            sendFriendRequest()
        }

    }

}