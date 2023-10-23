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
import com.example.blinkupnewmodule.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubmitPasscode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_passcode)

        val loading = findViewById<View>(R.id.loading)

        val submitPasscodeButton = findViewById<Button>(R.id.submit_passcode_button)
        submitPasscodeButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                val passcode = findViewById<EditText>(R.id.submit_signup_passcode).text.toString()
                try {
                    val user = Blinkup.confirmCode(passcode)
                    Log.i("user", "${user.phoneNumber}")
                } catch (e: BlinkupException) {
                    Log.e("confirmCode", "failed to run confirmCode", e)
                    return@launch
                }
                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@SubmitPasscode, ButtonsPage::class.java)
                    startActivity(intent)
                }
            }
        }
    }


}