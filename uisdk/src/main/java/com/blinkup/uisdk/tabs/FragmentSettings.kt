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
import androidx.lifecycle.lifecycleScope
import com.blinkup.uisdk.LoginActivity
import com.blinkup.uisdk.LoginActivity.Companion.user
import com.blinkup.uisdk.R
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.Blinkup.Companion.checkSessionAndLogin
import com.blinkupapp.sdk.BuildConfig
import com.blinkupapp.sdk.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSettings : BaseFragment() {
    private lateinit var user: User

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


        view.findViewById<Button>(R.id.save_changes).setOnClickListener {
            lifecycleScope.launch {
                Blinkup.updateUser(view.findViewById<EditText>(R.id.user_name).text.toString(), view.findViewById<EditText>(R.id.account_name).text.toString())
            }
            Log.i("username", LoginActivity.user?.name.toString())
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
}