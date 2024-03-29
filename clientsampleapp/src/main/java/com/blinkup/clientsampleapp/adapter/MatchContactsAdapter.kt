package com.blinkup.clientsampleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.tabs.FragmentFriends
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty0


class MatchContactsAdapter(var data: List<ContactResult>) :AbstractAdapter<MatchContactsAdapter.MyViewHolder>() {

    private var phoneContacts = data.filterNot {
        it.name == null
    }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        var onSentRequest: (contact: ContactResult) -> Unit):
        RecyclerView.ViewHolder(view) {

        private val contactName: TextView
        private val sendRequest: ImageButton

        private var targetId : User = User()

        init{
            contactName = view.findViewById(R.id.name)
            sendRequest = view.findViewById(R.id.send_request_button)
        }

        fun bind(contact: ContactResult) {

            contactName.text = contact.name

            sendRequest.setOnClickListener {
                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        targetId.id = contact.userId
                        targetId.phoneNumber = contact.phoneNumber
                        Blinkup.sendFriendRequest(targetId)
                        launch(Dispatchers.Main) {
                            onSentRequest(contact)
                            getFriends()
                            Toast.makeText(view.context, "Request Sent", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e: BlinkupException) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }

        private fun getFriends() {
            getFriends()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false)
        return MyViewHolder(view, lifecycleOwner, ::onSentRequest)
    }

    private fun onSentRequest(contact: ContactResult) {
        phoneContacts = phoneContacts.filterNot {it == contact}
    }

    override fun getItemCount(): Int {
        return phoneContacts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(phoneContacts[position])
    }

}