package com.example.sampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FriendsAtEventList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: FriendsAtEventAdapter
    private lateinit var friendsAtEventList: List<User>
    private lateinit var userId: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_at_event_list)

        val loading = findViewById<View>(R.id.loading)
        val spinnerId = findViewById<Spinner>(R.id.dropdown_menu)


        friendsAtEventList = listOf()
        userId = User()
        manager = LinearLayoutManager(this)

        loading.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {

            try {
                userId = Blinkup.checkSessionAndLogin()

                val places = Blinkup.getEvents()

                var eventNames = ArrayList<String>()

                for (name in places) {
                    val name = name.name!!
                    eventNames.add(name)
                }



                lifecycleScope.launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                    val arrayAdp = ArrayAdapter(this@FriendsAtEventList, android.R.layout.simple_spinner_dropdown_item, eventNames)
                    spinnerId.adapter = arrayAdp

                    spinnerId?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {

                            lifecycleScope.launch(Dispatchers.IO) {

                                try {

                                    val placeId = Blinkup.getEvents()[position]
                                    friendsAtEventList = Blinkup.getUsersAtEvent(placeId)

                                    launch(Dispatchers.Main) {
                                        Log.i("friends present", "manager context: $manager")
                                        myAdapter = FriendsAtEventAdapter(friendsAtEventList, userId)
                                        recyclerView = findViewById<RecyclerView>(R.id.friends_list_at_event).apply {
                                            layoutManager = manager
                                            adapter = myAdapter
                                        }
                                    }
                                } catch (e: BlinkupException) {
                                    return@launch
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }

                }

            } catch (e: BlinkupException) {
                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
                Log.e("EventList", "failed to run getFriendsAtEventList", e)
                return@launch
            }
        }
    }
}