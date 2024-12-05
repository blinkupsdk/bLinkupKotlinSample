package com.blinkup.uisdk.base

import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blinkup.uisdk.R
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
            findViewById<View>(R.id.root)?.let {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.connect_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.done -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}