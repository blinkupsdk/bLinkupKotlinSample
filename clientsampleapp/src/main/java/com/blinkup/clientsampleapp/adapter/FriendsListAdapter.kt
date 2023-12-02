package com.blinkup.clientsampleapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.App
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsListAdapter(var data: List<UserWithPresence>, var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var filteredItems = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val userIdView: TextView = view.findViewById(R.id.user_id)
        private val isHere: ImageView = view.findViewById(R.id.isHere)
        private val root = view

        fun bind(user: UserWithPresence, viewType: ViewType) {
            nameView.text = user.user?.name
            userIdView.text = user.user?.id
            isHere.visibility = if (user.isPresent) View.VISIBLE else View.GONE

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

    class TailViewHolder(view: View, var context: Context) : RecyclerView.ViewHolder(view) {

        fun bind() {

            val dialogBuilder = AlertDialog.Builder(context)
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            matchPhoneContacts.setOnClickListener {

                dialogBuilder.setView(layout)
                dialogBuilder.setTitle("TEMP TITLE")

                val editName = EditText(context)

                layout.addView(editName)

                dialogBuilder.setNegativeButton("Close") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()

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
            TailViewHolder(view, context)
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