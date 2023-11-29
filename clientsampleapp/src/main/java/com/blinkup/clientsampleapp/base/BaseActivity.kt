package com.blinkup.clientsampleapp.base

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkup.clientsampleapp.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {
    fun showLoading() = lifecycleScope.launch(Dispatchers.Main) {
        findViewById<View>(R.id.loading)?.visibility = View.VISIBLE
    }

    fun hideLoading() = lifecycleScope.launch(Dispatchers.Main) {
        findViewById<View>(R.id.loading)?.visibility = View.GONE
    }

    fun showErrorMessage(message: String) {
        showSnackbar(message, Color.RED)
    }

    fun showMessage(message: String) {
        showSnackbar(message, Color.GREEN)
    }

    private fun showSnackbar(message: String, color: Int) =
        lifecycleScope.launch(Dispatchers.Main) {
            findViewById<View>(R.id.container)?.let {
                val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                snackbar.view.setBackgroundColor(color)
                snackbar.show()
            }
        }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment !is OnBackPressed || !fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }
}