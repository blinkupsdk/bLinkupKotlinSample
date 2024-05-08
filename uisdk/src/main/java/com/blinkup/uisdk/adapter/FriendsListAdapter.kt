package com.blinkup.uisdk.adapter

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
import com.blinkup.uisdk.R
import com.blinkup.uisdk.data.UserWithPresence
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
) : AbstractAdapter<RecyclerView.ViewHolder>() {
    var filteredItems = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class ViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        val onDeleteOrBlock: (user: UserWithPresence) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val nameBoldView: TextView = view.findViewById(R.id.nameBold)
        private val userName: TextView = view.findViewById(R.id.user_name)
        private val isHere: ImageView = view.findViewById(R.id.isHere)
        private val root = view

        fun bind(user: UserWithPresence) {
            nameView.text = user.user?.name
            userName.text = user.user?.id
            isHere.visibility = if (user.isPresent) View.VISIBLE else View.GONE
            nameBoldView.visibility = if (user.isPresent) View.VISIBLE else View.GONE
            nameView.visibility = if (user.isPresent) View.GONE else View.VISIBLE

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_list_item, parent, false)
        return ViewHolder(view, lifecycleOwner, ::onDeleteOrBlock)

    }

    private fun onDeleteOrBlock(userWithPresence: UserWithPresence) {
        filteredItems = filteredItems.filterNot { it == userWithPresence }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(filteredItems[position])
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
}