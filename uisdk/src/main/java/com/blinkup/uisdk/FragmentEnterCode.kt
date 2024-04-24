package com.blinkup.uisdk

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers

class FragmentEnterCode : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.submit_button).setOnClickListener {
            showLoading()
            view.findViewById<TextInputEditText>(R.id.sms_code).text?.toString()?.let { smsCode ->
                lifecycleScope.launch {
                    try {
                        val user = Blinkup.confirmCode(smsCode)
                        LoginActivity.user = user
                        openMainActivity()

                    } catch (e: Exception) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(view.context, "Oops! Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun openMainActivity() {
        activity?.let {
            it.finish()
            startActivity(Intent(it, MainActivity::class.java))
        }
    }
}