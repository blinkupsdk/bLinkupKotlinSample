package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.Blinkup.Companion.getEvents
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Place
import com.example.blinkupnewmodule.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PresenceTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presence_test)

        val checkPresenceTest = findViewById<Button>(R.id.check_presence)
        val isPresent = findViewById<Button>(R.id.is_present)
        val isNotPresent = findViewById<Button>(R.id.is_not_present)

        checkPresenceTest.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val event: List<Place> = getEvents()
                val place = event[0]
                try {
                    val isAtEvent = Blinkup.isUserAtEvent(place)
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@PresenceTest,
                            "Is At Event: ${isAtEvent}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: BlinkupException) {
                    Log.e("isAtEvent", "failed to run isUserAtEvent", e)
                    return@launch
                }
            }
        }
        isPresent.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val event: List<Place> = getEvents()
                val place: Place = event[0]
                try {
                    Blinkup.setUserAtEvent(true, place)
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@PresenceTest, "Is now at event", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: BlinkupException) {
                    Log.e("isAtEvent", "failed to run setUserAtEvent", e)
                    return@launch
                }
            }
        }
        isNotPresent.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val event: List<Place> = getEvents()
                Log.i("placeList", "$event")
                val place: Place = event[0]
                try {
                    Blinkup.setUserAtEvent(false, place)
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@PresenceTest,
                            "Is no longer at event",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: BlinkupException) {
                    Log.e("isAtEvent", "failed to run setUserOnEvent", e)
                    return@launch
                }
            }
        }
    }
}