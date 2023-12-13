package com.blinkup.clientsampleapp.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Connection
import com.blinkupapp.sdk.data.model.ConnectionRequest
import com.blinkupapp.sdk.data.model.ContactResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendsListAdapter(
    var data: List<UserWithPresence>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var filteredItems = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var lifecycleOwner: LifecycleOwner


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

    class TailViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        val showLoading: () -> Unit,
        val hideLoading: () -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        fun bind() {
            matchPhoneContacts.setOnClickListener {
                showDialog(DialogType.PHONE_CONTACTS)
            }
            pendingRequests.setOnClickListener {
                showDialog(DialogType.PENDING_REQUESTS)
            }
            blockedUsers.setOnClickListener {
                showDialog(DialogType.BLOCKED_USERS)
            }
        }

        fun showDialog(type: DialogType) {
            showLoading()

            var contacts: List<ContactResult>
            var requests: List<ConnectionRequest>
            var blockedUsers: List<Connection>
            var connectionList: List<Connection>

            val dialogBuilder = AlertDialog.Builder(view.context)
            val layout = LinearLayout(view.context)
            layout.orientation = LinearLayout.VERTICAL

            val recyclerView = RecyclerView(view.context)

            var adapter: AbstractAdapter<*>? = null
            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                when (type) {
                    DialogType.PHONE_CONTACTS -> {
                        try {
                            contacts = Blinkup.findContacts()
                            adapter = MatchContactsAdapter(contacts)
                        } catch (e: BlinkupException) {
                            //TODO
                        }
                    }

                    DialogType.PENDING_REQUESTS -> {
                        try {
                            requests = Blinkup.getFriendRequests()
                            adapter = PendingRequestAdapter(requests)
                        } catch (e: BlinkupException) {
                            //TODO
                        }
                    }

                    DialogType.BLOCKED_USERS -> {
                        try {
                            connectionList = Blinkup.getFriendList()
                            blockedUsers =
                                connectionList.filter { it.status.toString() == "blocked" }
                            adapter = BlockedListAdapter(blockedUsers)
                        } catch (e: BlinkupException) {
                            //TODO
                        }
                    }
                }


                launch(Dispatchers.Main) {
                    adapter?.lifecycleOwner = lifecycleOwner

                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(view.context)

                    layout.addView(recyclerView)

                    dialogBuilder.setView(layout)
                    dialogBuilder.setTitle(type.title)

                    dialogBuilder.setNegativeButton("Close")
                    { dialog, _ ->
                        dialog.cancel()
                    }

                    dialogBuilder.create().show()
                    hideLoading()
                }
            }
        }

        private val matchPhoneContacts
                : TextView = view.findViewById(R.id.matchPhoneContacts)
        private val pendingRequests
                : TextView = view.findViewById(R.id.pendingRequests)
        private val blockedUsers
                : TextView = view.findViewById(R.id.blockedUsers)
    }

    enum class DialogType(val title: String) {
        PHONE_CONTACTS("Phone Contacts"),
        PENDING_REQUESTS("Pending Requests"),
        BLOCKED_USERS("Blocked Users");
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_item, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_tail, parent, false)
            TailViewHolder(view, lifecycleOwner, showLoading, hideLoading)
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