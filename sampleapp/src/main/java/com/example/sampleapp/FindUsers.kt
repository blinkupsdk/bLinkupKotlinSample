package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindUsers : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var searchList: List<User>
    private lateinit var userId: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_users)

        val loading = findViewById<View>(R.id.loading)
        searchList = listOf()
        userId = User()
        manager = LinearLayoutManager(this)

        val searchButton = findViewById<Button>(R.id.submit_user_search_button)

        searchButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                val searchQuery = findViewById<TextView>(R.id.user_search_field).text.toString()
                try {

                    userId = Blinkup.checkSessionAndLogin()
                    searchList = Blinkup.findUsers(searchQuery).filter{it.id != userId.id && it.name != null}
                    Log.i("quickcheck", "$searchList")

                    launch(Dispatchers.Main) {
                        loading.visibility = View.GONE
                    }

                    launch(Dispatchers.Main) {
                        myAdapter = UserListAdapter(searchList, object : OnSendRequestListener {
                            override fun onSendRequest(user: User) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        Blinkup.sendFriendRequest(user)
                                    } catch (e: BlinkupException) {
                                        Log.e("deleteConnection", "failed to run requestCode", e)
                                    }
                                }
                            }
                        })
                        recyclerView = findViewById<RecyclerView>(R.id.user_list).apply {
                            layoutManager = manager
                            adapter = myAdapter
                        }
                    }

                } catch (e: BlinkupException) {
                    Log.e("UserList", "failed to run findUsers()", e)
                    return@launch
                }
            }
        }
    }
}

interface OnSendRequestListener {
    fun onSendRequest(user: User)
}