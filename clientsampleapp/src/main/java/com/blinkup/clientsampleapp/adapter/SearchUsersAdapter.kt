package com.blinkup.clientsampleapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchUsersAdapter(
    var data: List<User>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit):
    AbstractAdapter<SearchUsersAdapter.MyViewHolder>() {

    private var users = data.filterNot {
        //this temporary filter will prevent users with deleted accounts from appearing
        //on a list
        it.name == null
    }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(val view: View, val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(view) {

        private val userName: TextView
        private val sendRequest: ImageButton

        private var targetId : User = User()

        init{
            userName = view.findViewById(R.id.name)
            sendRequest = view.findViewById(R.id.send_request_button)
        }

        fun bind(user: User) {

            userName.text = user.name

            sendRequest.setOnClickListener {
                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        targetId.id = user.id
                        Blinkup.sendFriendRequest(targetId)
                        launch(Dispatchers.Main) {
                            Toast.makeText(view.context, "Request Sent", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e: BlinkupException) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_user_item, parent, false)
        return MyViewHolder(view, lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(users[position])
    }

}