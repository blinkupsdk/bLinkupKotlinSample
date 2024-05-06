package com.blinkup.uisdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.uisdk.adapter.MatchContactsAdapter
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentContacts : BaseFragment() {
    private var adapter: MatchContactsAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.contacts_recycler_view)
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            requireActivity().onBackPressed()
        }
        fillContacts()
    }

    private fun fillContacts() {
        lifecycleScope.launch(Dispatchers.IO) {
            adapter = MatchContactsAdapter(Blinkup.findContacts())
            withContext(Dispatchers.Main) {
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter = adapter
            }
        }


    }
}