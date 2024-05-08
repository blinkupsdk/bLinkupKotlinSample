package com.blinkup.uisdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.uisdk.R
import com.blinkupapp.sdk.data.model.ConnectionRequest

class RequestsListAdapter(
    var incomingRequests: List<ConnectionRequest>,
    var sentRequests: List<ConnectionRequest>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit,
) : AbstractAdapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    class ItemViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(view) {
        private var username: TextView? = null
        private var name: TextView? = null
        private var reply: ImageView? = null
        private var delete: ImageView? = null

        init {
            name = view.findViewById(R.id.name)
            username = view.findViewById(R.id.user_name)
            reply = view.findViewById(R.id.reply)
            delete = view.findViewById(R.id.delete)
        }

        fun bind(request: ConnectionRequest, isIncoming: Boolean) {
            name?.text = if (isIncoming) request.sourceUser?.name else request.targetUser?.name
            username?.text = if (isIncoming) request.sourceUser?.id else request.targetUser?.id
            reply?.visibility = if (isIncoming) View.VISIBLE else View.GONE
            delete?.visibility = if (isIncoming) View.GONE else View.VISIBLE

        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTitle: TextView = view as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.request_header_item, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.request_list_item, parent, false)
            ItemViewHolder(view, lifecycleOwner)
        }
    }

    override fun getItemCount(): Int {
        return incomingRequests.size + sentRequests.size + 2 // +2 for the headers
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == incomingRequests.size + 1) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.headerTitle.text =
                if (position == 0) "Pending Friend Requests" else "Pending Sent Requests"
        } else if (holder is ItemViewHolder) {
            val isIncoming = position <= incomingRequests.size
            val itemPosition = if (isIncoming) position - 1 else position - 2
            val request =
                if (position <= incomingRequests.size) incomingRequests[itemPosition] else sentRequests[itemPosition]
            holder.bind(request, isIncoming)
        }
    }
}