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
import com.blinkupapp.sdk.data.model.Connection
import com.blinkupapp.sdk.data.model.ConnectionStatus
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: FriendListAdapter
    private lateinit var friendList: List<Connection>
    private lateinit var userId: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        val loading = findViewById<View>(R.id.loading)
        friendList = listOf()
        userId = User()
        manager = LinearLayoutManager(this)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                friendList =
                    Blinkup.getFriendList().filter { it.status == ConnectionStatus.CONNECTED }
                userId = Blinkup.checkSessionAndLogin()

                val place1 = Blinkup.getEvents()
                val logcheck = Blinkup.getUsersAtEvent(place1[0])
                Log.i("test1", "$logcheck")

                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }

                launch(Dispatchers.Main) {
                    myAdapter = FriendListAdapter(friendList, userId, object : OnDeleteListener {
                        override fun onDelete(connection: Connection) {
                            loading.visibility = View.VISIBLE
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    Blinkup.updateConnection(connection, ConnectionStatus.BLOCKED)
                                    friendList = Blinkup.getFriendList()
                                        .filter { it.status == ConnectionStatus.CONNECTED }
                                    launch(Dispatchers.Main) {
                                        myAdapter.data = friendList
                                        myAdapter.notifyDataSetChanged()
                                    }
                                    launch(Dispatchers.Main) {
                                        loading.visibility = View.GONE
                                    }
                                } catch (e: BlinkupException) {
                                    Log.e("deleteConnection", "failed to run requestCode", e)
                                }
                            }
                        }

                    })
                    recyclerView = findViewById<RecyclerView>(R.id.friends_list).apply {
                        layoutManager = manager
                        adapter = myAdapter
                    }
                }

            } catch (e: BlinkupException) {
                Log.e("FriendList", "failed to run getFriendsList", e)
                return@launch
            }
        }
    }
}

interface OnDeleteListener {
    fun onDelete(connection: Connection)
}