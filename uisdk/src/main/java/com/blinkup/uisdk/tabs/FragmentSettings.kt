package com.blinkup.uisdk.tabs

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.uisdk.LoginActivity
import com.blinkup.uisdk.LoginActivity.Companion.user
import com.blinkup.uisdk.R
import com.blinkup.uisdk.adapter.AbstractAdapter
import com.blinkup.uisdk.adapter.BlockedListAdapter
import com.blinkup.uisdk.adapter.FriendsListAdapter
import com.blinkup.uisdk.adapter.MatchContactsAdapter
import com.blinkup.uisdk.adapter.PendingRequestAdapter
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.Blinkup.Companion.checkSessionAndLogin
import com.blinkupapp.sdk.BuildConfig
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Block
import com.blinkupapp.sdk.data.model.ConnectionRequest
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSettings : BaseFragment() {
    private lateinit var user: User
    private val blockedListAdapter: BlockedListAdapter =
        BlockedListAdapter(emptyList(), ::showLoading, ::hideLoading)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.back_button).setOnClickListener {
            requireActivity().onBackPressed()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            user = checkSessionAndLogin()
            lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    view.findViewById<EditText>(R.id.user_name).setText(user.name)
                    view.findViewById<EditText>(R.id.account_name).setText(user.emailAddress)
                }
            }
        }

        fun showDialog(type: DialogType) {
            showLoading()

            var blockedUsers: List<Block>

            val dialogBuilder = AlertDialog.Builder(view.context)
            val layout = LinearLayout(view.context)
            layout.orientation = LinearLayout.VERTICAL

            val recyclerView = RecyclerView(view.context)

            var adapter: AbstractAdapter<*>? = null
            lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            blockedUsers = Blinkup.getBlocks()
                            adapter = BlockedListAdapter(blockedUsers, ::showLoading, ::hideLoading)
                        } catch (e: BlinkupException) {
                            launch(Dispatchers.Main) {
                                Toast.makeText(
                                    view.context,
                                    "Oops! Something went wrong",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }


                lifecycleScope.launch(Dispatchers.Main) {

                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(view.context)

                    layout.addView(recyclerView)

                    dialogBuilder.setView(layout)
                    dialogBuilder.setTitle(type.title)

                    dialogBuilder.setNegativeButton("Close")
                    { dialog, _ ->
                        dialog.cancel()
                    }

                    dialogBuilder.create().show()
                    hideLoading()
                }
            }

        view.findViewById<Button>(R.id.save_changes).setOnClickListener {
            lifecycleScope.launch {
                Blinkup.updateUser(
                    view.findViewById<EditText>(R.id.user_name).text.toString(),
                    view.findViewById<EditText>(R.id.account_name).text.toString()
                )
            }
            Log.i("username", LoginActivity.user?.name.toString())
        }

        view.findViewById<TextView>(R.id.manage_blocked_users).setOnClickListener { _ ->
            showDialog(FragmentSettings.DialogType.BLOCKED_USERS)
        }
            view.findViewById<TextView>(R.id.system_settings).setOnClickListener { _ ->
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }

//        view.findViewById<TextView>(R.id.delete_account).setOnClickListener { _ ->
//            val dialogBuilder = AlertDialog.Builder(context)
//            val layout = LinearLayout(context)
//            layout.orientation = LinearLayout.VERTICAL
//
//            dialogBuilder.setView(layout)
//            dialogBuilder.setTitle("Do you want to delete your account?")
//
//            dialogBuilder.setPositiveButton("OK") { _, _ ->
//                lifecycleScope.launch {
//                    //TODO remember to change this function name to whatever function deletes an account
//                    Blinkup.deleteUser()
//                    withContext(Dispatchers.Main) {
//                        val intent = Intent(context, LoginActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//            }
//
//            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
//                dialog.cancel()
//            }
//
//            dialogBuilder.create().show()
//        }

            view.findViewById<TextView>(R.id.sign_out).setOnClickListener { _ ->
                val dialogBuilder = AlertDialog.Builder(context)
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL

                dialogBuilder.setView(layout)
                dialogBuilder.setTitle("Do you want to log out?")

                dialogBuilder.setPositiveButton("OK") { _, _ ->
                    lifecycleScope.launch {
                        Blinkup.logout()
                        withContext(Dispatchers.Main) {
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
            }
        }
    enum class DialogType(val title: String) {
        BLOCKED_USERS("Blocked Users");
    }
}