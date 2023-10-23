package com.example.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        val submitInfo = findViewById<Button>(R.id.submit_info)
        val loading = findViewById<View>(R.id.loading)

        submitInfo.setOnClickListener {
            loading.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                val name = findViewById<EditText>(R.id.enter_name).text.toString()
                val email = findViewById<EditText>(R.id.enter_email).text.toString()
                try {
                    Blinkup.updateUser(name, email)
                    Log.i("UpdateUser", "updateUser: ${Blinkup.updateUser(name, email)}")
                } catch (e: BlinkupException) {
                    Log.e("updateUser", "failed to run updateUser", e)
                    return@launch
                }
                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@UpdateUser, FriendList::class.java)
                    startActivity(intent)
                }

            }
        }
    }
}