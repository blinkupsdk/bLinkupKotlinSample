package com.blinkup.uisdk

import FragmentNewUser
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
        view.findViewById<ImageView>(R.id.logo).setImageResource(LoginActivity.logoId)
        view.findViewById<TextView>(R.id.customerName).text = LoginActivity.customerName

        view.findViewById<View>(R.id.submit_button).setOnClickListener {
            showLoading()
            view.findViewById<TextInputEditText>(R.id.sms_code).text?.toString()?.let { smsCode ->
                lifecycleScope.launch {
                    try {
                        val user = Blinkup.confirmCode(smsCode)
                        LoginActivity.user = user
                        if (Blinkup.isUserDetailsRequired()) {
                            openNewUserFragment()
                        } else {
                            openMainActivity()
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

        view.findViewById<TextInputEditText>(R.id.sms_code).addTextChangedListener { text ->
            view.findViewById<View>(R.id.submit_button).isEnabled = text.toString().isNotEmpty()
        }

        view.findViewById<View>(R.id.cancel_button).setOnClickListener {
            onBackPressed()
        }

        arguments?.getString(PHONE)?.let {
            view.findViewById<TextView>(R.id.code_sent_to).text =
                String.format(getString(R.string.one_time_code_sent_to), it)
        }

    }

    private fun openNewUserFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentNewUser())
            .addToBackStack(null)
            .commit()
    }

    private fun openMainActivity() {
        activity?.let {
            startActivity(Intent(it, MainActivity::class.java))
            it.finish()
        }
    }

    companion object {
        const val PHONE: String = "PHONE"
    }
}