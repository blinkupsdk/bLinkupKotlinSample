package com.blinkup.clientsampleapp.tabs

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blinkup.clientsampleapp.App
import com.blinkup.clientsampleapp.R
import com.blinkup.clientsampleapp.adapter.AbstractAdapter
import com.blinkup.clientsampleapp.adapter.FriendsListAdapter
import com.blinkup.clientsampleapp.adapter.SearchUsersAdapter
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkup.clientsampleapp.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.ConnectionStatus
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkupapp.sdk.data.model.User
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentFriends() : BaseFragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private var friendsList: List<UserWithPresence> = emptyList()
    private lateinit var recyclerView: RecyclerView
    private val adapter: FriendsListAdapter = FriendsListAdapter(emptyList(), ::showLoading, ::hideLoading, ::getFriends)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            getFriends()
        }

        adapter.lifecycleOwner = requireActivity()

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        tabLayout = view.findViewById(R.id.tab_layout)
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

                searchUsers(query, view, requireActivity())

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
            val friends = Blinkup.getFriendList().filterNot {
                it.targetUser?.name == null || it.sourceUser?.name == null || it.status == ConnectionStatus.BLOCKED
            }
            val events = Blinkup.getEvents()
            val currentEvent = events.find { Blinkup.isUserAtEvent(it) }
            val friendsAtEvent = currentEvent?.let {
                Blinkup.getUsersAtEvent(it)
            } ?: emptyList()
            friendsList = friends
                .map { connection ->
                val user = if (connection.targetUser?.id == App.user?.id) {
                    connection.sourceUser
                } else {
                    connection.targetUser
                }
                UserWithPresence(
                    user,
                    friendsAtEvent.find { it.user?.id == user?.id }?.isPresent ?: false,
                    connection
                ) }
            tabSelected(tabLayout.selectedTabPosition)
        } catch (e: Exception) {
            showErrorMessage(e.message ?: "Unknown error")
        } finally {
            hideLoading()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getAllFriends(): List<UserWithPresence> {
        return friendsList
    }

    private fun getPresentFriends(): List<UserWithPresence> {
        return friendsList.filter { it.isPresent }
    }

    private fun searchUsers(query: String?, view: View, lifecycleOwner: LifecycleOwner) {

        var users: List<User>

        val dialogBuilder = AlertDialog.Builder(view.context)
        val layout = LinearLayout(view.context)
        layout.orientation = LinearLayout.VERTICAL

        val searchRecyclerView = RecyclerView(view.context)

        lifecycleScope.launch(Dispatchers.IO) {

            users = Blinkup.findUsers(query)
            val searchAdapter = SearchUsersAdapter(users, ::showLoading, ::hideLoading)

            launch(Dispatchers.Main) {

                searchAdapter.lifecycleOwner = lifecycleOwner

                searchRecyclerView.adapter = searchAdapter
                searchRecyclerView.layoutManager = LinearLayoutManager(view.context)

                layout.addView(searchRecyclerView)

                dialogBuilder.setView(layout)
                dialogBuilder.setTitle("Searched Users")

                dialogBuilder.setNegativeButton("Close")
                { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
            }

        }
    }

    fun updatePresence() {
        getFriends()
    }

}