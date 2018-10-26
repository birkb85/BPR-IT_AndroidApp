package com.bprit.app.bprit.models

import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import com.bprit.app.bprit.R
import android.widget.TextView


/**
 * Loading AlertDialog
 */
class LoadingAlertDialog(private var activity: FragmentActivity) {

    var isLoading = false
    var loadingAlertDialog: AlertDialog? = null
    var loadingTextView: TextView? = null
    var loadingText = ""

    init {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val inflaterView = inflater.inflate(R.layout.dialog_loading, null)
        alertDialogBuilder.setView(inflaterView)
        alertDialogBuilder.setCancelable(false)
        loadingAlertDialog = alertDialogBuilder.create()
        loadingTextView = inflaterView.findViewById(R.id.textView)
    }

    /**
     * Set loading
     * @param activity context of activity / fragment
     * @param isLoading if loading dialog should show / dismiss
     * @param text text to be displayed in loading dialog
     */
    fun setLoading(activity: FragmentActivity, isLoading: Boolean, text: String) {
        activity.runOnUiThread {
            this.isLoading = isLoading
            this.loadingText = text

            if (isLoading) {
                loadingTextView?.text = loadingText
                loadingAlertDialog?.let { lad ->
                    if (!lad.isShowing) lad.show()
                }
            } else {
                loadingAlertDialog?.dismiss()
            }
        }
    }

    /**
     * Run at on resume event for activity / fragment
     */
    fun onResume() {
        loadingAlertDialog?.let { lad ->
            if (isLoading && !lad.isShowing) {
                lad.show()
            }
        }
    }

    /**
     * Run at on pause event for activity / fragment
     */
    fun onPause() {
        loadingAlertDialog?.let { lad ->
            if (lad.isShowing) {
                lad.dismiss()
            }
        }
    }
}