package com.example.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.User

class FriendsAtEventAdapter(
    var data: List<User>,
    var userId: User
) : RecyclerView.Adapter<FriendsAtEventAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        private val friendsView: TextView

        init {
            friendsView = view.findViewById<TextView>(R.id.friends_at_event_item)
        }

        fun bind(user: User, currentUserId: User) {
            friendsView.text =
                user.name
                if (user.id == currentUserId.id) {
                    friendsView.visibility = View.GONE
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_at_event_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position], userId)
    }

}