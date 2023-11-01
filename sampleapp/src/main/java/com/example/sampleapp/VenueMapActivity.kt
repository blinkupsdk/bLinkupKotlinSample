package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Place
import com.blinkupapp.sdk.data.model.User
import com.blinkupapp.sdk.view.VenueMapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VenueMapActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var places: List<Place>
    private lateinit var placeNames: List<String?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_map)

        val loading = findViewById<View>(R.id.loading)
        val map = findViewById<VenueMapView>(R.id.map)

        loading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            places = Blinkup.getEvents()
            placeNames = places.map{ it.name }
            Log.i("placenames", "$placeNames")
            launch(Dispatchers.Main) {
                map.place = places[0]
            }
            launch(Dispatchers.Main) {
                loading.visibility = View.GONE
            }
            launch(Dispatchers.Main) {
                loading.visibility = View.GONE
            }
            myAdapter = VenueMapAdapter(placeNames)
        }
    }
}