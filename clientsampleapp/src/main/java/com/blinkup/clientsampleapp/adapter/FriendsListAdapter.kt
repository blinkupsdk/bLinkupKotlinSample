package com.blinkup.clientsampleapp.adapter

import android.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Block
import com.blinkupapp.sdk.data.model.ConnectionRequest
import com.blinkupapp.sdk.data.model.ConnectionStatus
import com.blinkupapp.sdk.data.model.ContactResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsListAdapter(
    var data: List<UserWithPresence>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit,
    val getFriends: () -> Unit
) : AbstractAdapter<RecyclerView.ViewHolder>() {
    var filteredItems = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class ViewHolder(val view: View,
                     val lifecycleOwner: LifecycleOwner,
                     val onDeleteOrBlock: (user: UserWithPresence) -> Unit):
                     RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val userIdView: TextView = view.findViewById(R.id.user_id)
        private val userNameUnderlined: TextView = view.findViewById(R.id.nameUnderlined)
        private val isHere: ImageView = view.findViewById(R.id.isHere)
        private val root = view
        private val openMenu: ImageButton = view.findViewById(R.id.optionsMenu)

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

            openMenu.setOnClickListener {
                showOptions(user)
            }

        }

        fun showOptions(userWithPresence: UserWithPresence) {
            val optionMenu = PopupMenu(view.context, view, Gravity.END)
            optionMenu.inflate(R.menu.menu_items)
                optionMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.unfriend -> {
                            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                                try {
                                    Blinkup.deleteConnection(userWithPresence.connection)
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(view.context, "User unfriended", Toast.LENGTH_LONG).show()
                                        onDeleteOrBlock(userWithPresence)
                                    }
                                } catch (e: BlinkupException){
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                                        onDeleteOrBlock(userWithPresence)
                                    }
                                }
                            }
                            true
                        }
                        R.id.block -> {
                            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                                try {
                                    Blinkup.blockUser(userWithPresence.user!!)
                                    Blinkup.updateConnection(userWithPresence.connection, ConnectionStatus.BLOCKED)
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(view.context, "User blocked", Toast.LENGTH_LONG).show()
                                        onDeleteOrBlock(userWithPresence)
                                    }
                                } catch (e: BlinkupException){
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }

            optionMenu.show()
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
        val hideLoading: () -> Unit,
        val getFriends: () -> Unit
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
            var blockedUsers: List<Block>

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
                            launch(Dispatchers.Main) {
                                Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    DialogType.PENDING_REQUESTS -> {
                        try {
                            requests = Blinkup.getFriendRequests()
                            adapter = PendingRequestAdapter(requests, getFriends)
                        } catch (e: BlinkupException) {
                            launch(Dispatchers.Main) {
                                Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    DialogType.BLOCKED_USERS -> {
                        try {
                            blockedUsers = Blinkup.getBlocks()
                            adapter = BlockedListAdapter(blockedUsers, getFriends)
                        } catch (e: BlinkupException) {
                            launch(Dispatchers.Main) {
                                Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                            }
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
            ViewHolder(view, lifecycleOwner, ::onDeleteOrBlock)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_tail, parent, false)
            TailViewHolder(view, lifecycleOwner, showLoading, hideLoading, getFriends)
        }
    }

    private fun onDeleteOrBlock(userWithPresence: UserWithPresence) {
        filteredItems = filteredItems.filterNot {it == userWithPresence}
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