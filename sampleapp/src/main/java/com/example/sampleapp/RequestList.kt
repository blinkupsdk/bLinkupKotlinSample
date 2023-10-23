package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.ConnectionRequest
import com.blinkupapp.sdk.data.model.User
import com.example.blinkupnewmodule.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RequestsListAdapter
    private lateinit var requestList: List<ConnectionRequest>
    private lateinit var userId: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)

        val loading = findViewById<View>(R.id.loading)
        requestList = listOf()
        userId = User()
        manager = LinearLayoutManager(this)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                requestList = Blinkup.getFriendRequests()
                userId = Blinkup.checkSessionAndLogin()

                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }

                launch(Dispatchers.Main) {
                    myAdapter = RequestsListAdapter(requestList, userId, object : OnAcceptListener {
                        override fun onAccept(connection: ConnectionRequest) {
                            loading.visibility = View.VISIBLE
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    Blinkup.acceptFriendRequest(connection)
                                    Blinkup.denyFriendRequest(connection)
                                    requestList = Blinkup.getFriendRequests()
                                    launch(Dispatchers.Main) {
                                        myAdapter.data = requestList
                                        myAdapter.notifyDataSetChanged()
                                        loading.visibility = View.GONE
                                    }
                                } catch (e: BlinkupException) {
                                    Log.e("deleteConnection", "failed to run requestCode", e)
                                }
                            }
                        }

                    },
                        object : OnDenyListener {
                            override fun onDeny(connection: ConnectionRequest) {
                                loading.visibility = View.VISIBLE
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        Blinkup.denyFriendRequest(connection)
                                        requestList = Blinkup.getFriendRequests()
                                        launch(Dispatchers.Main) {
                                            myAdapter.data = requestList
                                            myAdapter.notifyDataSetChanged()
                                            loading.visibility = View.GONE
                                        }
                                    } catch (e: BlinkupException) {
                                        Log.e("deleteConnection", "failed to run requestCode", e)
                                    }
                                }
                            }

                        },
                        object : OnCancelListener {
                            override fun onCancel(connection: ConnectionRequest) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        Blinkup.denyFriendRequest(connection)
                                        requestList = Blinkup.getFriendRequests()
                                        launch(Dispatchers.Main) {
                                            myAdapter.data = requestList
                                            myAdapter.notifyDataSetChanged()
                                        }
                                    } catch (e: BlinkupException) {
                                        Log.e("deleteConnection", "failed to run requestCode", e)
                                    }
                                }
                            }

                        })
                    recyclerView = findViewById<RecyclerView>(R.id.request_list).apply {
                        layoutManager = manager
                        adapter = myAdapter
                    }
                }

            } catch (e: BlinkupException) {
                Log.e("RequestList", "failed to run getFriendRequests", e)
                return@launch
            }
        }
    }
}

interface OnAcceptListener {
    fun onAccept(connection: ConnectionRequest)
}

interface OnDenyListener {
    fun onDeny(connection: ConnectionRequest)
}

interface OnCancelListener {
    fun onCancel(connection: ConnectionRequest)
}