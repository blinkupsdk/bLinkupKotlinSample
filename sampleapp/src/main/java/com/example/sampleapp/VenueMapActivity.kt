package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.view.VenueMapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VenueMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_map)

        val loading = findViewById<View>(R.id.loading)
        val map = findViewById<VenueMapView>(R.id.map)
        val spinnerId = findViewById<Spinner>(R.id.dropdown_menu)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val places = Blinkup.getEvents()

            val eventNames = ArrayList<String>()

            for (name in places) {
                val name = name.name!!
                eventNames.add(name)
            }

            launch(Dispatchers.Main) {
                val arrayAdp = ArrayAdapter(this@VenueMapActivity, android.R.layout.simple_spinner_dropdown_item, eventNames)
                spinnerId.adapter = arrayAdp

                spinnerId?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        map.place = places[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }

            }
            launch(Dispatchers.Main) {
                loading.visibility = View.GONE
            }
        }
    }
}