package com.blinkup.clientsampleapp

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.google.android.material.textfield.TextInputEditText
import com.permissionx.guolindev.PermissionX
import android.Manifest
import android.content.Context
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.blinkup.clientsampleapp.model.DemoClientIds
import com.blinkupapp.sdk.data.exception.BlinkupException
import kotlinx.coroutines.Dispatchers

class FragmentEnterPhone : BaseFragment() {
    private val selectedClientIdKey: String = "selectedClientId"
    private var selectedClientId: String? = null
        set(value) {
            field = value
            view?.findViewById<TextInputEditText>(R.id.phone_number)?.isEnabled = value != null
            view?.findViewById<View>(R.id.submit_button)?.isEnabled = value != null
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.READ_CONTACTS
            )
            .onExplainRequestReason { scope, rationale ->
                scope.showRequestReasonDialog(
                    rationale,
                    "Location permissions need to be set to Allow All The Time for Geofences to operate",
                    "Ok",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(view.context, "All permissions are granted", Toast.LENGTH_LONG)
                        .show()
                    login(view)
                } else {
                    Toast.makeText(
                        view.context,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                    login(view)
                }
            }

    }

    private fun openEnterCodeFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentEnterCode())
            .addToBackStack(null)
            .commit()
    }

    private fun openMainActivity() {
        activity?.let {
            it.finish()
            startActivity(Intent(it, MainActivity::class.java))
        }
    }

    private fun login(view: View) {

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        val clientIdSpinner: Spinner = view.findViewById(R.id.clientIdSpinner)

        var clientNames = DemoClientIds.clientIds.map { it.name }

        val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, clientNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        clientIdSpinner.adapter = adapter


        selectedClientId = sharedPref?.getString(selectedClientIdKey, "")
        val selectedIndex = clientNames.indexOf(selectedClientId)
        if (selectedIndex != -1) {
            clientIdSpinner.setSelection(selectedIndex)
        }

        clientIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedClientId = clientNames[position]

                with(sharedPref?.edit()) {
                    this?.putString(selectedClientIdKey, selectedClientId)
                    this?.apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        if (Blinkup.isLoginRequired()) {
            view.findViewById<View>(R.id.submit_button).setOnClickListener {
                showLoading()
                view.findViewById<TextInputEditText>(R.id.phone_number).text?.toString()
                    ?.let { phone ->
                        lifecycleScope.launch {
                            try {
                                DemoClientIds.clientIds.find { it.name == selectedClientId }
                                    ?.let { clientId ->
                                        Blinkup.requestCode(clientId.id, phone)
                                        openEnterCodeFragment()
                                    }


                            } catch (e: Exception) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        view.context,
                                        "Oops! Something went wrong",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } finally {
                                hideLoading()
                            }
                        }
                    }
            }


        } else {
            showLoading()
            lifecycleScope.launch {
                try {
                    App.user = Blinkup.checkSessionAndLogin()
                    openMainActivity()
                } catch (e: Exception) {
                    showErrorMessage(e.message ?: "Something went wrong")
                } finally {
                    hideLoading()
                }
            }
        }
    }
}