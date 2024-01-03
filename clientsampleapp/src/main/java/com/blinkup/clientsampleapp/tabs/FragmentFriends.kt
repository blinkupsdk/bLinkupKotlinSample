package com.blinkup.clientsampleapp.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.App
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.adapter.FriendsListAdapter
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkup.clientsampleapp.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentFriends() : BaseFragment() {
    private lateinit var searchView: SearchView
    private var friendsList: List<UserWithPresence> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private val adapter: FriendsListAdapter = FriendsListAdapter(emptyList(), ::showLoading, ::hideLoading)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.lifecycleOwner = requireActivity()

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
        searchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // do nothing
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return false
            }
        })
        getFriends()
    }

    private fun tabSelected(position: Int) = lifecycleScope.launch(Dispatchers.Main) {
        when (position) {
            0 -> {
                adapter.data = getAllFriends()
                adapter.filter(searchView.query.toString())
            }

            1 -> {
                adapter.data = getPresentFriends()
                adapter.filter(searchView.query.toString())
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun getFriends() = lifecycleScope.launch(Dispatchers.IO) {
        try {
            showLoading()
            val friends = Blinkup.getFriendList()
            val events = Blinkup.getEvents()
            val currentEvent = events.find { Blinkup.isUserAtEvent(it) }
            val friendsAtEvent = currentEvent?.let {
                Blinkup.getUsersAtEvent(it)
            } ?: emptyList()
            Log.i("eventfriends", "$friendsAtEvent")
            friendsList = friends.map { connection ->
                val user = if (connection.targetUser?.id == App.user?.id) {
                    connection.sourceUser
                } else {
                    connection.targetUser
                }
                UserWithPresence(
                    user,
                    friendsAtEvent.find { it.user?.id == user?.id }?.isPresent ?: false
                )
            }
            tabSelected(0)
        } catch (e: Exception) {
            showErrorMessage(e.message ?: "Unknown error")
        } finally {
            hideLoading()
        }
    }

    private fun getAllFriends(): List<UserWithPresence> {
        return friendsList
    }

    private fun getPresentFriends(): List<UserWithPresence> {
        Log.i("listcheck", "$friendsList")
        return friendsList.filter { it.isPresent }
    }

}