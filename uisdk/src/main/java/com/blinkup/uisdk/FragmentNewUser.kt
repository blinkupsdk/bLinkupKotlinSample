import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.blinkup.uisdk.MainActivity
import com.blinkup.uisdk.R
import com.blinkup.uisdk.base.BaseFragment
import com.blinkupapp.sdk.Blinkup
import com.google.android.material.textfield.TextInputLayout
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch

class FragmentNewUser : BaseFragment() {

    private lateinit var contactsSwitch: SwitchCompat
    private lateinit var userNameLayout: TextInputLayout
    private lateinit var nameLayout: TextInputLayout
    private lateinit var submitButton: AppCompatButton

    private fun updateUser() {
        showLoading()
        val userName = userNameLayout.editText?.text.toString()
        val name = nameLayout.editText?.text.toString()

        lifecycleScope.launch {
            try {
                val user = Blinkup.updateUser(userName, name)
                openMainActivity()
            } catch (e: Exception) {
                showErrorMessage("Oops! Something went wrong")
            } finally {
                hideLoading()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_user, container, false)

        userNameLayout = view.findViewById(R.id.user_name_layout)
        nameLayout = view.findViewById(R.id.name_layout)
        submitButton = view.findViewById(R.id.submit_button)
        contactsSwitch = view.findViewById(R.id.contacts_switch)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        userNameLayout.editText?.addTextChangedListener(textWatcher)
        nameLayout.editText?.addTextChangedListener(textWatcher)

        submitButton.setOnClickListener {
            if (contactsSwitch.isChecked) {
                requestContactsPermission()
            } else {
                updateUser()
            }
        }

        return view
    }

    private fun requestContactsPermission() {
        val context = view?.context ?: return
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_CONTACTS,
            )
            .onExplainRequestReason { scope, rationale ->
                scope.showRequestReasonDialog(
                    rationale,
                    "Contacts permissions is required to match your contacts with the users at events",
                    "Ok",
                    "Cancel"
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    Toast.makeText(context, "All permissions are granted", Toast.LENGTH_LONG)
                        .show()
                    updateUser()
                } else {
                    Toast.makeText(
                        context,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                    updateUser()
                }
            }


    }

    private fun checkFieldsForEmptyValues() {
        val userName = userNameLayout.editText?.text.toString()
        val name = nameLayout.editText?.text.toString()

        submitButton.isEnabled = userName.isNotEmpty() && name.isNotEmpty()
    }

    private fun openMainActivity() {
        activity?.let {
            it.finish()
            startActivity(Intent(it, MainActivity::class.java))
        }
    }
}