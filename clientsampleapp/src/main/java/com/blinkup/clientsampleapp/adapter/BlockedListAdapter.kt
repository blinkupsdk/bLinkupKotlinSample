package com.blinkup.clientsampleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Block
import com.blinkupapp.sdk.data.model.Connection
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BlockedListAdapter(var data: List<Block>) :
    AbstractAdapter<BlockedListAdapter.MyViewHolder>() {

    private var blockedUsers = data.filterNot {
        //this temporary filter will prevent users with deleted accounts from appearing
        //on a list
        it.blockee.name == null
    }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(val view: View, val lifecycleOwner: LifecycleOwner) :
        RecyclerView.ViewHolder(view) {


        private val contactName: TextView
        private val userId: TextView
        private val unblockUser: Button

        private var targetId: User = User()

        init {
            contactName = view.findViewById(R.id.name)
            userId = view.findViewById(R.id.user_id)
            unblockUser = view.findViewById(R.id.unblock_user_button)
        }

        fun bind(blockedUser: Block) {

            contactName.text = blockedUser.blockee?.name
            userId.text = blockedUser.blockee?.name

            unblockUser.setOnClickListener {
                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        Blinkup.unblockUser(blockedUser)
                        launch(Dispatchers.Main) {
                            Toast.makeText(view.context, "User unblocked", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: BlinkupException) {

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.blocked_list_item, parent, false)
        return MyViewHolder(view, lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return blockedUsers.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(blockedUsers[position])
    }

}