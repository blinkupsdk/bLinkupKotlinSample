package com.example.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.Contact
import com.blinkupapp.sdk.data.model.User

class SearchContactsAdapter(
    val data: List<Contact>,
    val userId: User,
    val onSendContactRequestListener: OnSendContactRequestListener
) : RecyclerView.Adapter<SearchContactsAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val contactsView: TextView
        private val connectButton: Button


        init {
            contactsView = view.findViewById<TextView>(R.id.contact_item)
            connectButton = view.findViewById(R.id.connect_button)
        }

        fun bind(contact: Contact, userId: User, onSendContactRequestListener: OnSendContactRequestListener) {
            contactsView.text = "temp"
            connectButton.setOnClickListener {
                onSendContactRequestListener.onSendRequest(userId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position], userId, onSendContactRequestListener)
    }

}