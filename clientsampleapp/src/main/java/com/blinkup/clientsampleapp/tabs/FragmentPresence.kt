package com.blinkup.clientsampleapp.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.App
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.adapter.AbstractAdapter
import com.blinkup.clientsampleapp.adapter.EventsListAdapter
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.Presence
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentPresence : BaseFragment() {
    private var eventsList: List<Presence> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private var adapter: AbstractAdapter<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_presence, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Present"))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabSelected(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // do nothing
            }
        })
        getEvents()
    }

    private fun tabSelected(position: Int) = lifecycleScope.launch(Dispatchers.Main) {
        var data = emptyList<Presence>()
        when (position) {
            0 -> {
                data = getAllEvents()
                adapter = EventsListAdapter(data, ::showLoading, ::hideLoading)
            }

            1 -> {
                data = getPresentEvents()
                adapter = EventsListAdapter(data, ::showLoading, ::hideLoading)
            }
        }
    }

    private fun getEvents() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            showLoading()
            val events = Blinkup.getEvents()
            eventsList = events.map {
                Presence(
                    place = it,
                    isPresent = Blinkup.isUserAtEvent(it),
                )
            }
            tabSelected(0)
        } catch (e: Exception) {
            showErrorMessage(e.message ?: "Unknown error")
        } finally {
            hideLoading()
        }
    }

    private fun getAllEvents(): List<Presence> {
        return eventsList
    }

    private fun getPresentEvents(): List<Presence> {
        return eventsList.filter { it.isPresent == true }
    }
}
