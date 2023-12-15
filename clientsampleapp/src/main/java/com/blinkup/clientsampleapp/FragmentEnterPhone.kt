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
import android.widget.Toast

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

        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(view.context, "All permissions are granted", Toast.LENGTH_LONG).show()
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
        if (Blinkup.isLoginRequired()) {
            view.findViewById<View>(R.id.submit_button).setOnClickListener {
                showLoading()
                view.findViewById<TextInputEditText>(R.id.phone_number).text?.toString()
                    ?.let { phone ->
                        lifecycleScope.launch {
                            try {
                                Blinkup.requestCode(phone)
                                showMessage("Code sent")
                                openEnterCodeFragment()

                            } catch (e: Exception) {
                                showErrorMessage(e.message ?: "Something went wrong")
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