package com.blinkup.uisdk.tabs

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.blinkup.uisdk.LoginActivity
import com.blinkup.uisdk.R
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSettings : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.account_name).text = LoginActivity.user?.name
        view.findViewById<TextView>(R.id.account_number).text = LoginActivity.user?.phoneNumber

        view.findViewById<ImageView>(R.id.edit_icon).setOnClickListener { _ ->
            val dialogBuilder = AlertDialog.Builder(context)
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            dialogBuilder.setView(layout)
            dialogBuilder.setTitle("Update Name")

            val editName = EditText(context)
            editName.hint = LoginActivity.user?.name

            layout.addView(editName)

            dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                val newName = editName.text.toString()
                lifecycleScope.launch {
                    LoginActivity.user = Blinkup.updateUser(newName, LoginActivity.user?.emailAddress)
                    launch(Dispatchers.Main) {
                        view.findViewById<TextView>(R.id.account_name).text = LoginActivity.user?.name
                    }
                }
                dialog.dismiss()
            }

            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            dialogBuilder.create().show()
        }

        view.findViewById<TextView>(R.id.privacy_policy).setOnClickListener { _ ->
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.blinkupapp.com/privacy-policy"))
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.terms_of_service).setOnClickListener { _ ->
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.blinkupapp.com/terms-and-conditions"))
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.delete_account).setOnClickListener { _ ->
            val dialogBuilder = AlertDialog.Builder(context)
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            dialogBuilder.setView(layout)
            dialogBuilder.setTitle("Do you want to delete your account?")

            dialogBuilder.setPositiveButton("OK") { _, _ ->
                lifecycleScope.launch {
                    //TODO remember to change this function name to whatever function deletes an account
                    Blinkup.deleteUser()
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

        view.findViewById<TextView>(R.id.log_out).setOnClickListener { _ ->
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