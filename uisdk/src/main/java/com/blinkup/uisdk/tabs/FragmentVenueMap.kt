package com.blinkup.uisdk.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinkup.uisdk.R
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.data.model.Place
import com.blinkupapp.sdk.view.VenueMapView
import com.google.gson.Gson

class FragmentVenueMap : BaseFragment() {
    companion object {
        private const val ARG_PLACE = "arg_place"

        fun newInstance(place: Place): FragmentVenueMap {
            val fragment = FragmentVenueMap()
            val args = Bundle()
            val gson = Gson()
            val placeJson = gson.toJson(place)
            args.putString(ARG_PLACE, placeJson)
            fragment.arguments = args
            return fragment
        }
    }

    private var place: Place? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_venue_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placeJson = arguments?.getString(ARG_PLACE)
        val gson = Gson()
        place = gson.fromJson(placeJson, Place::class.java)

        view.findViewById<VenueMapView>(R.id.map).place = place
    }
}