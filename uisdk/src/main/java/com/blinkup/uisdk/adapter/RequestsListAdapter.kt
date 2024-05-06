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

class RequestsListAdapter(
    var data: List<UserWithPresence>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit,
) : AbstractAdapter<RecyclerView.ViewHolder>() {


    class ViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        val onDeleteOrBlock: (user: UserWithPresence) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {


        fun bind(user: UserWithPresence, viewType: ViewType) {


        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

    }

    override fun getItemCount(): Int {
        return filteredItems.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}