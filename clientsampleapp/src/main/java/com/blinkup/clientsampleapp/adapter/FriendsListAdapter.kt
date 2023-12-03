package com.blinkup.clientsampleapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.data.UserWithPresence

class FriendsListAdapter(var data: List<UserWithPresence>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var filteredItems = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val userIdView: TextView = view.findViewById(R.id.user_id)
        private val userNameUnderlined: TextView = view.findViewById(R.id.nameUnderlined)
        private val isHere: ImageView = view.findViewById(R.id.isHere)
        private val root = view

        fun bind(user: UserWithPresence, viewType: ViewType) {
            nameView.text = user.user?.name
            userNameUnderlined.text = user.user?.name
            userIdView.text = user.user?.id
            isHere.visibility = if (user.isPresent) View.VISIBLE else View.GONE
            userNameUnderlined.visibility = if (user.isPresent) View.VISIBLE else View.GONE
            nameView.visibility = if (user.isPresent) View.GONE else View.VISIBLE

            when (viewType) {
                ViewType.TOP -> {
                    root.setBackgroundResource(R.drawable.rounded_corners_top)
                }

                ViewType.MIDDLE -> {
                    root.setBackgroundResource(R.drawable.bottom_border)
                }

                ViewType.BOTTOM -> {
                    root.setBackgroundResource(R.drawable.rounded_corners_bottom)
                }
            }
        }
    }

    enum class ViewType {
        TOP,
        MIDDLE,
        BOTTOM
    }

    class TailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            matchPhoneContacts.setOnClickListener {
                //TODO:open dialog to match contacts
            }
            pendingRequests.setOnClickListener {
                //TODO:open dialog to show pending requests
            }
            blockedUsers.setOnClickListener {
                //TODO:open dialog to show blocked users
            }
        }

        private val matchPhoneContacts: TextView = view.findViewById(R.id.matchPhoneContacts)
        private val pendingRequests: TextView = view.findViewById(R.id.pendingRequests)
        private val blockedUsers: TextView = view.findViewById(R.id.blockedUsers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.user_list_tail, parent, false)
            TailViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == filteredItems.size) VIEW_TYPE_TAIL else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return filteredItems.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(
                filteredItems[position], when (position) {
                    0 -> ViewType.TOP
                    filteredItems.size - 1 -> ViewType.BOTTOM
                    else -> ViewType.MIDDLE
                }
            )
        } else if (holder is TailViewHolder) {
            holder.bind()
        }
    }

    fun filter(query: String?) {
        filteredItems = if (query.isNullOrEmpty()) {
            data
        } else {
            data.filter {
                it.user?.name?.contains(query, true) ?: false ||
                        it.user?.id?.contains(query, true) ?: false
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_TAIL = 1
    }
}