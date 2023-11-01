package com.example.sampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsAtEventList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: FriendsAtEventAdapter
    private lateinit var friendsAtEventList: List<User>
    private lateinit var userId: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        val loading = findViewById<View>(R.id.loading)
        friendsAtEventList = listOf()
        userId = User()
        manager = LinearLayoutManager(this)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val placeId = Blinkup.getEvents()[0]
                friendsAtEventList =
                    Blinkup.getUsersAtEvent(placeId)
                userId = Blinkup.checkSessionAndLogin()

                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }

                launch(Dispatchers.Main) {
                    myAdapter = FriendsAtEventAdapter(friendsAtEventList, userId)
                    recyclerView = findViewById<RecyclerView>(R.id.friends_list).apply {
                        layoutManager = manager
                        adapter = myAdapter
                    }
                }

            } catch (e: BlinkupException) {
                Log.e("EventList", "failed to run getFriendsAtEventList", e)
                return@launch
            }
        }
    }
}