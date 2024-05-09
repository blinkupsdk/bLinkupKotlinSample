package com.blinkup.uisdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.uisdk.LoginActivity
import com.blinkup.uisdk.R
import com.blinkup.uisdk.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import kotlinx.coroutines.launch

class SearchUsersAdapter2(
    var friendsResults: List<UserWithPresence>,
    var otherResults: List<UserWithPresence>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit,
) : AbstractAdapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    class ItemViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        val showLoading: () -> Unit,
        val hideLoading: () -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        private var username: TextView? = null
        private var name: TextView? = null
        private var isHere: ImageView? = null
        private var addFriend: ImageView? = null

        init {
            name = view.findViewById(R.id.nameBold)
            username = view.findViewById(R.id.user_name)
            isHere = view.findViewById(R.id.isHere)
            addFriend = view.findViewById(R.id.addFriend)
        }

        fun bind(user: UserWithPresence, isFriend: Boolean) {
            name?.text = user.user?.name
            username?.text = user.user?.id
            isHere?.visibility = if (isFriend && user.isPresent) View.VISIBLE else View.GONE
            addFriend?.visibility = if (isFriend) View.GONE else View.VISIBLE


            view.setOnClickListener {
                if (!isFriend && user.user != null) {
                    val builder = AlertDialog.Builder(view.context)
                    builder.setTitle("Send Friend Request")
                    builder.setMessage("Send a friend request so you can see when each other are both at the ${LoginActivity.customerName} Locations at the same time.")
                    builder.setPositiveButton("Send") { dialog, _ ->
                        lifecycleOwner.lifecycleScope.launch {
                            showLoading()
                            try {
                                Blinkup.sendFriendRequest(user.user)
                            } catch (e: Exception) {
                                hideLoading()
                                return@launch
                            }
                            hideLoading()
                            dialog.dismiss()
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        // Handle No button click here
                        dialog.dismiss()
                    }
                    builder.create().show()

                }
            }
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
                .inflate(R.layout.user_search_list_item, parent, false)
            ItemViewHolder(view, lifecycleOwner, showLoading, hideLoading)
        }
    }

    override fun getItemCount(): Int {
        return friendsResults.size + otherResults.size + 2 // +2 for the headers
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == friendsResults.size + 1) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.headerTitle.text =
                if (position == 0) "Friends" else "Other results on Bucks Connect"
        } else if (holder is ItemViewHolder) {
            val isFriend = position <= friendsResults.size
            val itemPosition = if (isFriend) position - 1 else position - 2
            val user =
                if (position <= friendsResults.size) friendsResults[itemPosition] else otherResults[itemPosition]
            holder.bind(user, isFriend)
        }
    }
}