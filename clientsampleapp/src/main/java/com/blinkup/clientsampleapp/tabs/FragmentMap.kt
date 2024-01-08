package com.blinkup.clientsampleapp.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.adapter.MapsListAdapter
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentMap : BaseFragment() {
    private var placesList: List<Place> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private val adapter: MapsListAdapter = MapsListAdapter(emptyList(), ::onPlaceClick)

    private fun onPlaceClick(place: Place) {
        val fragment = FragmentVenueMap.newInstance(place)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        getPlaces()
    }

    private fun getPlaces() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            showLoading()
            placesList = Blinkup.getEvents()
            withContext(Dispatchers.Main) {
                adapter.data = placesList
            }
        } catch (e: Exception) {
            showErrorMessage(e.message ?: "Unknown error")
        } finally {
            hideLoading()
        }
    }
}
