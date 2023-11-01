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
import com.blinkupapp.sdk.data.model.Contact
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchContacts : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: SearchContactsAdapter
    private lateinit var contactList: List<ContactResult>
    private lateinit var userId: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_contacts)

        val loading = findViewById<View>(R.id.loading)
        contactList = listOf()
        userId = User()
        manager = LinearLayoutManager(this)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                contactList = Blinkup.findContacts()
                Log.i("findContacts", "contact list: $contactList")
//                userId = Blinkup.checkSessionAndLogin()

                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }

                launch(Dispatchers.Main) {
                    myAdapter = SearchContactsAdapter(contactList, userId, object : OnSendContactRequestListener {
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
                    recyclerView = findViewById<RecyclerView>(R.id.friends_list).apply {
                        layoutManager = manager
                        adapter = myAdapter
                    }
                }

            } catch (e: BlinkupException) {
                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
                Log.e("findContacts", "failed to run findContacts", e)
                return@launch
            }
        }
    }
}

interface OnSendContactRequestListener {
    fun onSendRequest(user: User)
}