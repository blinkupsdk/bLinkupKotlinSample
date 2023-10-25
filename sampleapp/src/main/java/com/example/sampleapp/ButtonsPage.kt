package com.example.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ButtonsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buttons_page)

        val searchUserButton = findViewById<Button>(R.id.go_to_user_search_button)
        val friendPage = findViewById<Button>(R.id.go_to_friends)
        val updateUserButton = findViewById<Button>(R.id.update_user_info)
        val friendRequests = findViewById<Button>(R.id.friend_requests)
        val blinkMap = findViewById<Button>(R.id.places)
        val presenceTest = findViewById<Button>(R.id.presence_test)
        val searchContacts = findViewById<Button>(R.id.search_contacts)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Blinkup.getEvents()
            } catch (e: BlinkupException) {
                Log.e("getEvents", "failed to run getEvents", e)
            }
        }

        searchUserButton.setOnClickListener {
            val intent = Intent(this@ButtonsPage, FindUsers::class.java)
            startActivity(intent)
        }
        friendPage.setOnClickListener {
            val intent = Intent(this@ButtonsPage, FriendList::class.java)
            startActivity(intent)
        }
        updateUserButton.setOnClickListener {
            val intent = Intent(this@ButtonsPage, UpdateUser::class.java)
            startActivity(intent)
        }
        friendRequests.setOnClickListener {
            val intent = Intent(this@ButtonsPage, RequestList::class.java)
            startActivity(intent)
        }
        blinkMap.setOnClickListener {
            val intent = Intent(this@ButtonsPage, VenueMapActivity::class.java)
            startActivity(intent)
        }
        presenceTest.setOnClickListener {
            val intent = Intent(this@ButtonsPage, PresenceTest::class.java)
            startActivity(intent)
        }
        searchContacts.setOnClickListener {
            val intent = Intent(this@ButtonsPage, SearchContacts::class.java)
            startActivity(intent)
        }
    }

}