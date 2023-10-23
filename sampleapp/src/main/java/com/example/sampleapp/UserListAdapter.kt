package com.example.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.User

class UserListAdapter(
    private val data: List<User>,
    val onSendRequestListener: OnSendRequestListener
) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val userView: TextView
        private val connectButton: Button

        init {
            userView = view.findViewById<TextView>(R.id.user_list_item)
            connectButton = view.findViewById<Button>(R.id.connect_button)
        }

        fun bind(user: User, onSendRequestListener: OnSendRequestListener) {
            userView.text = user.name
            connectButton.setOnClickListener {
                onSendRequestListener.onSendRequest(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position], onSendRequestListener)
    }

}