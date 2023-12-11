package com.blinkup.clientsampleapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.ConnectionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PendingRequestAdapter(var data: List<ConnectionRequest>): RecyclerView.Adapter<PendingRequestAdapter.MyViewHolder>() {

    private var requests = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var lifecycleOwner: LifecycleOwner
    class MyViewHolder(view: View, val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(view) {

        private val contactName: TextView
        val acceptRequest: Button
        val denyRequest: Button
        val cancelRequest: Button

        init{
            contactName = view.findViewById(R.id.name)
            acceptRequest = view.findViewById(R.id.accept_request_button)
            denyRequest = view.findViewById(R.id.deny_request_button)
            cancelRequest = view.findViewById(R.id.cancel_request_button)
        }

        fun bind(request: ConnectionRequest) {

            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val sourceUser = Blinkup.checkSessionAndLogin()

                    launch(Dispatchers.Main) {
                        contactName.text =
                            if(request.sourceUser?.id == sourceUser.id)
                                request.targetUser?.name else request.sourceUser?.name

                        Log.i("buttons", "source user id: ${request.sourceUser?.id}, match userId: ${(request.sourceUser?.id == sourceUser.id)}")

                        acceptRequest.visibility =
                            if(request.sourceUser?.id == sourceUser.id) View.GONE else View.VISIBLE
                        denyRequest.visibility =
                            if(request.sourceUser?.id == sourceUser.id) View.GONE else View.VISIBLE
                        cancelRequest.visibility =
                            if(request.sourceUser?.id == sourceUser.id) View.VISIBLE else View.GONE
                    }

                    acceptRequest.setOnClickListener {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                Blinkup.acceptFriendRequest(request)
                            } catch (e: BlinkupException) {
                                //TODO
                            }
                        }
                    }

                    denyRequest.setOnClickListener{
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                Blinkup.denyFriendRequest(request)
                            } catch (e: BlinkupException) {
                                //TODO
                            }
                        }
                    }

                    cancelRequest.setOnClickListener {
                        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                Blinkup.denyFriendRequest(request)
                            } catch (e: BlinkupException) {
                                //TODO
                            }
                        }
                    }


                } catch (e: BlinkupException){
                    //TODO
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_request_item, parent, false)
        return MyViewHolder(view, lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(requests[position])
    }

}