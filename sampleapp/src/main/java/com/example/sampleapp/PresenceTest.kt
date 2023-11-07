package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.Blinkup.Companion.getEvents
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PresenceTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presence_test)

        val loading = findViewById<View>(R.id.loading)
        val spinnerId = findViewById<Spinner>(R.id.dropdown_menu)
        val checkPresenceTest = findViewById<Button>(R.id.check_presence)
        val isPresent = findViewById<Button>(R.id.is_present)
        val isNotPresent = findViewById<Button>(R.id.is_not_present)

        loading.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            val places = Blinkup.getEvents()

            val eventNames = ArrayList<String>()

            for (name in places) {
                val name = name.name!!
                eventNames.add(name)
            }

            launch(Dispatchers.Main) {
                val arrayAdp = ArrayAdapter(this@PresenceTest, android.R.layout.simple_spinner_dropdown_item, eventNames)
                spinnerId.adapter = arrayAdp

                spinnerId?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val place = places[position]
                            Log.i("isAtEvent", "places call: $place")

                            checkPresenceTest.setOnClickListener {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val isAtEvent = Blinkup.isUserAtEvent(place)
                                        launch(Dispatchers.Main) {
                                            Toast.makeText(
                                                this@PresenceTest,
                                                "Is At Event: ${isAtEvent}",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: BlinkupException) {
                                        Log.e("isAtEvent", "failed to run isUserAtEvent", e)
                                        launch(Dispatchers.Main) {
                                            loading.visibility = View.GONE
                                        }
                                        return@launch
                                    }
                                }
                            }

                            isPresent.setOnClickListener {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        Blinkup.setUserAtEvent(true, place)
                                        launch(Dispatchers.Main) {
                                            Toast.makeText(
                                                this@PresenceTest,
                                                "Is now at event",
                                                Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    } catch (e: BlinkupException) {
                                        Log.e("isAtEvent", "failed to run setUserAtEvent", e)
                                        launch(Dispatchers.Main) {
                                            loading.visibility = View.GONE
                                        }
                                        return@launch
                                    }
                                }

                            }

                            isNotPresent.setOnClickListener {
                                lifecycleScope.launch(Dispatchers.IO) {
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
                                        launch(Dispatchers.Main) {
                                            loading.visibility = View.GONE
                                        }
                                        Log.e("isAtEvent", "failed to run setUserOnEvent", e)
                                        return@launch
                                    }
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }

                launch(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }

            }

        }


//        checkPresenceTest.setOnClickListener {
//            lifecycleScope.launch(Dispatchers.IO) {
//                val event: List<Place> = getEvents()
//                val place = event[0]
//                try {
//                    val isAtEvent = Blinkup.isUserAtEvent(place)
//
//                } catch (e: BlinkupException) {
//                    Log.e("isAtEvent", "failed to run isUserAtEvent", e)
//                    return@launch
//                }
//            }
//        }
//        isPresent.setOnClickListener {
//            lifecycleScope.launch(Dispatchers.IO) {
//                val event: List<Place> = getEvents()
//                val place: Place = event[0]
//                try {
//                    Blinkup.setUserAtEvent(true, place)
//                    launch(Dispatchers.Main) {
//                        Toast.makeText(this@PresenceTest, "Is now at event", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                } catch (e: BlinkupException) {
//                    Log.e("isAtEvent", "failed to run setUserAtEvent", e)
//                    return@launch
//                }
//            }
//        }
//        isNotPresent.setOnClickListener {
//            lifecycleScope.launch(Dispatchers.IO) {
//                val event: List<Place> = getEvents()
//                Log.i("placeList", "$event")
//                val place: Place = event[0]
//                try {
//                    Blinkup.setUserAtEvent(false, place)
//                    launch(Dispatchers.Main) {
//                        Toast.makeText(
//                            this@PresenceTest,
//                            "Is no longer at event",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                    }
//                } catch (e: BlinkupException) {
//                    Log.e("isAtEvent", "failed to run setUserOnEvent", e)
//                    return@launch
//                }
//            }
//        }
    }
}