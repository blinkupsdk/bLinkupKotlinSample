package com.blinkup.uisdk

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.google.android.material.textfield.TextInputEditText
import com.permissionx.guolindev.PermissionX
import android.Manifest
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.Dispatchers

class FragmentEnterPhone : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.logo).setImageResource(LoginActivity.logoId)
        view.findViewById<TextView>(R.id.customerName).text = LoginActivity.customerName

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
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

    private fun openEnterCodeFragment(phone: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, with(FragmentEnterCode()){
                arguments = Bundle().apply {
                    putString(FragmentEnterCode.PHONE, phone)
                }
                this

            })
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
        if (Blinkup.isLoginRequired()) {
            view.findViewById<View>(R.id.submit_button).setOnClickListener {
                showLoading()
                view.findViewById<TextInputEditText>(R.id.phone_number).text?.toString()
                    ?.let { phone ->
                        lifecycleScope.launch {
                            try {
                                LoginActivity.clientId?.let { clientId ->
                                    Blinkup.requestCode(clientId, phone)
                                    openEnterCodeFragment(phone)
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
            view.findViewById<View>(R.id.cancel_button).setOnClickListener {
                onBackPressed()
            }

            view.findViewById<TextInputEditText>(R.id.phone_number).addTextChangedListener { text ->
                view.findViewById<View>(R.id.submit_button).isEnabled = text.toString().isNotEmpty()
            }
        } else {
            showLoading()
            lifecycleScope.launch {
                try {
                    LoginActivity.user = Blinkup.checkSessionAndLogin()
                    openMainActivity()
                } catch (e: Exception) {
                    showErrorMessage(e.message ?: "Something went wrong")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    companion object {
        const val ID: String = "id"
        const val NAME: String = "name"
    }
}