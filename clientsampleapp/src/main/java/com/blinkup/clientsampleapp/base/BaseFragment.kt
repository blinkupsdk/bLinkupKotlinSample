package com.blinkup.clientsampleapp.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), OnBackPressed {
    override fun onBackPressed(): Boolean {
        return false
    }

    fun showLoading() {
        activity?.let {
            if (it is BaseActivity) {
                it.showLoading()
            }
        }
    }

    fun hideLoading() {
        activity?.let {
            if (it is BaseActivity) {
                it.hideLoading()
            }
        }
    }

    fun showErrorMessage(message: String) {
        activity?.let {
            if (it is BaseActivity) {
                it.showErrorMessage(message)
            }
        }
    }

    fun showMessage(message: String) {
        activity?.let {
            if (it is BaseActivity) {
                it.showMessage(message)
            }
        }
    }
}