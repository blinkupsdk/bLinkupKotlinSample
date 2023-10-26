package com.example.sampleapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.permissionx.guolindev.BuildConfig
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        val API_KEY: String = com.example.sampleapp.BuildConfig.API_KEY
        Log.i("keycheck", "$API_KEY")
        Blinkup.init(API_KEY, this)

        //find loading view by id
        val loading = findViewById<View>(R.id.loading)

        val loginButton = findViewById<Button>(R.id.log_in_button)
        loginButton.setOnClickListener {
            //show the loading layout.
            // no need to put it into coroutine with Dispatchers.Main because onclick listeners are processed in main thread
            loading.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                val phoneNumber = findViewById<TextView>(R.id.log_in_text_field).text.toString()
                try {
                    Blinkup.requestCode(phoneNumber)
                    Log.i("response", Blinkup.requestCode(phoneNumber))
                } catch (e: BlinkupException) {
                    Log.e("requestCode", "failed to run requestCode", e)
                    return@launch
                }
                //now hiding the loading. Here we need to launch it with MAIN dispatcher because it is in the coroutine running in IO dispatcher
                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@MainActivity, SubmitPasscode::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}