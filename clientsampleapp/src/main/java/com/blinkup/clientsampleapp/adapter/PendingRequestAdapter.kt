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
import com.blinkupapp.sdk.data.model.ConnectionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty0

class PendingRequestAdapter(var data: List<ConnectionRequest>, val getFriends: () -> Unit): AbstractAdapter<PendingRequestAdapter.MyViewHolder>() {
    private var requests = data.filterNot {
        it.targetUser?.name == null || it.sourceUser?.name == null
    }
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    class MyViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        var onRequestResponded: (request: ConnectionRequest) -> Unit,
        var getFriends: () -> Unit
    ):
        RecyclerView.ViewHolder(view) {

        private val contactName: TextView
        val acceptRequest: ImageButton
        val denyRequest: ImageButton
        val cancelRequest: ImageButton

        init{
            contactName = view.findViewById(R.id.name)
            acceptRequest = view.findViewById(R.id.accept_request_button)
            denyRequest = view.findViewById(R.id.deny_request_button)
            cancelRequest = view.findViewById(R.id.cancel_request_button)
        }

        fun bind(request: ConnectionRequest) {

            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val currentUser = Blinkup.checkSessionAndLogin()

                    launch(Dispatchers.Main) {
                        contactName.text =
                            if(request.sourceUser?.id == currentUser.id)
                                request.targetUser?.name else request.sourceUser?.name

                        Log.i("buttons", "source user id: ${request.sourceUser?.id}, match userId: ${(request.sourceUser?.id == currentUser.id)}")

                        acceptRequest.visibility =
                            if(request.sourceUser?.id == currentUser.id) View.GONE else View.VISIBLE
                        denyRequest.visibility =
                            if(request.sourceUser?.id == currentUser.id) View.GONE else View.VISIBLE
                        cancelRequest.visibility =
                            if(request.sourceUser?.id == currentUser.id) View.VISIBLE else View.GONE
                    }

                    acceptRequest.setOnClickListener {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                Blinkup.acceptFriendRequest(request)
                                Blinkup.denyFriendRequest(request)
                                launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "Friend Request accepted", Toast.LENGTH_LONG).show()
                                    onRequestResponded(request)
                                    getFriends()
                                }
                            } catch (e: BlinkupException) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    denyRequest.setOnClickListener{
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                Blinkup.denyFriendRequest(request)
                                launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "Friend Request denied", Toast.LENGTH_LONG).show()
                                    onRequestResponded(request)
                                }
                            } catch (e: BlinkupException) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    cancelRequest.setOnClickListener {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                Blinkup.denyFriendRequest(request)
                                launch(Dispatchers.Main) {
                                    onRequestResponded(request)
                                    Toast.makeText(view.context, "Friend Request cancelled", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: BlinkupException) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }


                } catch (e: BlinkupException){
                    launch(Dispatchers.Main) {
                        Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                    }
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_request_item, parent, false)
        return MyViewHolder(view, lifecycleOwner, ::onRequestAccepted, getFriends)
    }

    private fun onRequestAccepted(request: ConnectionRequest) {
        requests = requests.filterNot {it == request}
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(requests[position])
    }

}