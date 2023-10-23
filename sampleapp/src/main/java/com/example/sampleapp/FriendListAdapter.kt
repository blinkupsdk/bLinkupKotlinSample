package com.example.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.Connection
import com.blinkupapp.sdk.data.model.User


class FriendListAdapter(
    var data: List<Connection>,
    var userId: User,
    private val onDeleteListener: OnDeleteListener
) : RecyclerView.Adapter<FriendListAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        private val friendsView: TextView
        private val deleteButton: ImageButton


        init {
            friendsView = view.findViewById<TextView>(R.id.friend_list_item)
            deleteButton = view.findViewById<ImageButton>(R.id.delete_friend_button)
        }

        fun bind(connection: Connection, userId: User, onDeleteListener: OnDeleteListener) {
            friendsView.text =
                if (connection.targetUser?.id == userId.id) connection.sourceUser?.name else connection.targetUser?.name
            deleteButton.setOnClickListener {
                onDeleteListener.onDelete(connection)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position], userId, onDeleteListener)
    }

}