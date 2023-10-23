package com.blinkup.sampleapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.view.VenueMapView
import com.example.blinkupnewmodule.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VenueMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_map)

        val loading = findViewById<View>(R.id.loading)
        val map = findViewById<VenueMapView>(R.id.map)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val places = Blinkup.getEvents()
            launch(Dispatchers.Main) {
                map.place = places[0]
            }
            launch(Dispatchers.Main) {
                loading.visibility = View.GONE
            }
        }
    }
}