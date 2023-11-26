package com.blinkup.clientsampleapp

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blinkup.clientsampleapp.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.google.android.material.textfield.TextInputEditText

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

        view.findViewById<View>(R.id.submit_button).setOnClickListener {
            showLoading()
            view.findViewById<TextInputEditText>(R.id.phone_number).text?.toString()?.let { phone ->
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
    }

    private fun openEnterCodeFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentEnterCode())
            .addToBackStack(null)
            .commit()
    }
}