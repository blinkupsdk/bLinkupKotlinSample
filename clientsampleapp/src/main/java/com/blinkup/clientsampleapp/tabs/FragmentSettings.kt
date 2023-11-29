package com.blinkup.clientsampleapp.tabs

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.model.User
import com.blinkup.clientsampleapp.ViewPagerAdapter
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

        user = User()

        lifecycleScope.launch {
            try {
                user = Blinkup.checkSessionAndLogin()

                launch(Dispatchers.Main) {
                    view.findViewById<TextView>(R.id.account_name).text = user.name
                    view.findViewById<TextView>(R.id.account_number).text = user.phoneNumber
                }

            } catch (e: Exception) {
                showErrorMessage(e.message ?: "Something went wrong")
            } finally {
                hideLoading()
            }
        }

        view.findViewById<ImageView>(R.id.edit_icon).setOnClickListener { v ->
            val dialogBuilder = AlertDialog.Builder(context)
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            dialogBuilder.setView(layout)
            dialogBuilder.setTitle("Update Name")

            val editName = EditText(context)
            editName.hint = user.name

            layout.addView(editName)

            dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                val newName = editName.text.toString()
                lifecycleScope.launch {
                    user = Blinkup.updateUser(newName, user.emailAddress)
                    launch(Dispatchers.Main) {
                        view.findViewById<TextView>(R.id.account_name).text = user.name
                    }
                }
                dialog.dismiss()
            }

            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            dialogBuilder.create().show()
        }

        view.findViewById<TextView>(R.id.privacy_policy).setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.youtube.com"))
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.terms_of_service).setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.google.com"))
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.log_out).setOnClickListener { view ->
            val dialogBuilder = AlertDialog.Builder(context)
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            dialogBuilder.setView(layout)
            dialogBuilder.setTitle("Do you want to log out?")

            dialogBuilder.setPositiveButton("OK") { dialog, _ ->
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
    fun reloadFragment(fragmentManager: FragmentManager, fragment: Fragment) {

            fragmentManager.beginTransaction()
                .detach(fragment)
                .commit()

        val newFragment = FragmentSettings()


        fragmentManager.beginTransaction()
            .add(R.id.view_pager, newFragment)
            .commit()
    }
}