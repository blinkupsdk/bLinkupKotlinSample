package com.blinkup.uisdk.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blinkup.uisdk.FragmentContacts
import com.blinkup.uisdk.FragmentMapNew
import com.blinkup.uisdk.LoginActivity
import com.blinkup.uisdk.R
import com.blinkup.uisdk.adapter.FriendsListAdapter
import com.blinkup.uisdk.base.BaseFragment
import com.blinkup.uisdk.data.UserWithPresence
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.ConnectionStatus
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
    private val friendsAdapter: FriendsListAdapter =
        FriendsListAdapter(emptyList(), ::showLoading, ::hideLoading, ::getFriends)
    private val requestsAdapter: RequestsListAdapter = RequestsListAdapter(emptyList(), ::showLoading, ::hideLoading, ::getFriends)

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

        friendsAdapter.lifecycleOwner = requireActivity()

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = friendsAdapter


        tabLayout = view.findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("Friends Here"))
        tabLayout.addTab(tabLayout.newTab().setText("All Friends"))
        tabLayout.addTab(tabLayout.newTab().setText("Requests"))
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
                friendsAdapter.filter(newText)
                return false
            }
        })

        searchView.setOnSearchClickListener {
            tabLayout.visibility = View.GONE
            searchUsers(null, view, requireActivity())

        }
        searchView.setOnCloseListener {
            tabLayout.visibility = View.VISIBLE
            false
        }

        view.findViewById<View>(R.id.map_button).setOnClickListener {
            val fragment = FragmentMapNew()
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<View>(R.id.contacts_button).setOnClickListener {
            val fragment = FragmentContacts()
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        getFriends()
    }

    private fun tabSelected(position: Int) = lifecycleScope.launch(Dispatchers.Main) {
        when (position) {
            0 -> {
                friendsAdapter.data = getPresentFriends()
                friendsAdapter.notifyDataSetChanged()
            }

            1 -> {
                friendsAdapter.data = getAllFriends()
                friendsAdapter.notifyDataSetChanged()
            }

            2 -> {
                friendsAdapter.data =
                    friendsList.filter { it.connection?.status == ConnectionStatus.PENDING }
            }
        }

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
                    val user = if (connection.targetUser?.id == LoginActivity.user?.id) {
                        connection.sourceUser
                    } else {
                        connection.targetUser
                    }
                    UserWithPresence(
                        user,
                        friendsAtEvent.find { it.user?.id == user?.id }?.isPresent ?: false,
                        connection
                    )
                }
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

        val searchRecyclerView = RecyclerView(view.context)

        lifecycleScope.launch(Dispatchers.IO) {

            users = query?.let { Blinkup.findUsers(query) } ?: emptyList()
//            val searchAdapter = SearchUsersAdapter(users, ::showLoading, ::hideLoading)

            launch(Dispatchers.Main) {

                friendsAdapter.data = users.map { UserWithPresence(it, false, null) }
                friendsAdapter.notifyDataSetChanged()
//                searchAdapter.lifecycleOwner = lifecycleOwner
//
//                searchRecyclerView.adapter = searchAdapter
//                searchRecyclerView.layoutManager = LinearLayoutManager(view.context)
//
//                layout.addView(searchRecyclerView)
//
//                dialogBuilder.setView(layout)
//                dialogBuilder.setTitle("Searched Users")
//
//                dialogBuilder.setNegativeButton("Close")
//                { dialog, _ ->
//                    dialog.cancel()
//                }
//
//                dialogBuilder.create().show()
            }

        }
    }

    fun updatePresence() {
        getFriends()
    }

}