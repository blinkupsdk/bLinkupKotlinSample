package com.blinkup.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.data.model.ConnectionRequest
import com.blinkupapp.sdk.data.model.User
import com.example.blinkupnewmodule.R

class RequestsListAdapter(
    var data: List<ConnectionRequest>,
    val userId: User,
    val onAcceptListener: OnAcceptListener,
    val onDenyListener: OnDenyListener,
    val onCancelListener: OnCancelListener
) : RecyclerView.Adapter<RequestsListAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        private val requestView: TextView
        private val acceptRequest: Button
        private val denyRequest: Button
        private val cancelRequest: Button

        init {
            requestView = view.findViewById<TextView>(R.id.request_list_item)
            acceptRequest = view.findViewById<Button>(R.id.accept_request_button)
            denyRequest = view.findViewById<Button>(R.id.deny_request_button)
            cancelRequest = view.findViewById<Button>(R.id.cancel_request_button)
        }

        fun bind(
            connection: ConnectionRequest,
            userId: User,
            onAcceptListener: OnAcceptListener,
            onDenyListener: OnDenyListener,
            onCancelListener: OnCancelListener
        ) {

            requestView.text =
                if (connection.sourceUser?.id == userId.id) connection.targetUser?.name else connection.sourceUser?.name

            acceptRequest.visibility =
                if (connection.sourceUser?.id == userId.id) View.GONE else View.VISIBLE
            denyRequest.visibility =
                if (connection.sourceUser?.id == userId.id) View.GONE else View.VISIBLE
            cancelRequest.visibility =
                if (connection.sourceUser?.id == userId.id) View.VISIBLE else View.GONE

            cancelRequest.setOnClickListener {
                onCancelListener.onCancel(connection)
            }

            acceptRequest.setOnClickListener {
                onAcceptListener.onAccept(connection)
            }

            denyRequest.setOnClickListener {
                onDenyListener.onDeny(connection)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.request_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position], userId, onAcceptListener, onDenyListener, onCancelListener)
    }

}