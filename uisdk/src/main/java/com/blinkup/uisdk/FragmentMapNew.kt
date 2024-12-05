package com.blinkup.uisdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.Place
import com.blinkupapp.sdk.view.VenueMapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentMapNew : BaseFragment() {
    private var maps: List<Place>? = null
    private var venueMap: VenueMapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        venueMap = view.findViewById(R.id.map)

        view.findViewById<View>(R.id.change_map_button).setOnClickListener {
            changeMap()
        }
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            requireActivity().onBackPressed()
        }


        loadEvents()
    }

    private fun loadEvents() {
        showLoading()
        lifecycleScope.launch(Dispatchers.IO) {
            maps = Blinkup.getEvents()
            withContext(Dispatchers.Main) {
                hideLoading()
                venueMap?.place = maps?.firstOrNull()
            }
        }
    }

    private fun changeMap() {
        val placeNames = maps?.map { it.name }?.toTypedArray()
        var checkedItem = 0 // this will checked the item when user open the dialog
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.choose_a_map)
                .setSingleChoiceItems(placeNames, checkedItem) { _, which ->
                    checkedItem = which
                }
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    venueMap?.place = maps?.get(checkedItem)
                    dialog.dismiss()
                }
                .setNegativeButton(com.blinkupapp.sdk.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }
}