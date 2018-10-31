package com.bprit.app.bprit.models

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import android.widget.TextView
import android.app.Activity
import android.support.v7.app.AlertDialog
import android.widget.Button
import com.bprit.app.bprit.R
import com.bprit.app.bprit.interfaces.AlertDialogButtonOnClickListener
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.util.Log
import java.lang.Exception
import android.R.attr.onClick
import android.view.LayoutInflater
import android.view.View


/**
 * Global methods.
 */
class Global : Application() {
    companion object {
        /**
         * Preference file key for saving to local preferences.
         */
        fun getPreferenceFileKey(): String = "SharedPreferences"

        /**
         * Time interval when selecting time in spinner.
         */
        fun getMinuteTimeInterval(): Int = 5

        /**
         * Flag showing if user is signed in or not.
         */
        var isSignedIn: Boolean = false

        /**
         * Global instance of the Azure AD connection, accessible from all other classes.
         */
        var azureAD: AzureAD? = null
    }

    /**
     * Global class initialised when application is launched.
     * Initialise global accessible variables here.
     */
    override fun onCreate() {
        super.onCreate()

        // Init AzureAD
        if (azureAD == null) {
            azureAD = AzureAD()
        }

        Realm.init(this)
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"

        // Debug realm
//        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // Only while developing the app!!!

        // Release realm
        // To make an migration bump "schemaVersion" 1 up and write the changes in the method "realmMigration".
        // TODO Public version has schema version 0! When next version is made public set schema version to 1 and collect migrations in "oldVersion == 0".
        val config = RealmConfiguration.Builder().schemaVersion(0).migration(realmMigration).build()
        Realm.setDefaultConfiguration(config)
    }

    /**
     *  Handles migrations in release version.
     */
    private var realmMigration: RealmMigration = RealmMigration { realm, oldVersion, newVersion ->
        // More info at: https://realm.io/docs/java/latest/#migrations
        val schema = realm.schema

        // ----------- DO NOT EDIT MIGRATIONS ABOVE THIS LINE --------------

        // Example on migration
//        if (oldVersion < 1) {
//            schema.create("AzureADGraphResponse")
//                .addField("idToken", String::class.java)
//        }
    }

    /**
     * User friendly message for error.
     * @param context context of activity.
     * @param error http status code / error message.
     * @return friendly message.
     */
    private fun errorMessage(
        context: Context,
        error: String
    ): String {
        var message = ""

        if (error.contains("No address associated with hostname", true)) {
            message = context.getString(R.string.dialog_errorNoAddressAssociatedWithHostName)
        }

        if (message == "") {
            when (error) {
                "" -> message = "" // OK
                "304" -> message = "$error ${context.getString(R.string.dialog_error304)}"
                "400" -> message = "$error ${context.getString(R.string.dialog_error400)}"
                "401" -> message = "$error ${context.getString(R.string.dialog_error401)}"
                "404" -> message = "$error ${context.getString(R.string.dialog_error404)}"
                "500" -> message = "$error ${context.getString(R.string.dialog_error500)}"
                "503" -> message = "$error ${context.getString(R.string.dialog_error503)}"
                else -> message = context.getString(R.string.dialog_errorDefault)
            }
        }

        message = ("${context.getString(R.string.dialog_error)} $message").trim()

        return message
    }

    /**
     * Default dialog showing an error, is using friendly errors for error codes.
     * @param context context of activity.
     * @param error error code / text to display in alert dialog.
     * @param onClickListener callback when clicking on button.
     * @return alert dialog.
     */
    fun getErrorAlertDialog(
        context: Context,
        error: String,
        onClickListener: AlertDialogButtonOnClickListener?
    ): AlertDialog {
        return getMessageAlertDialog(context, errorMessage(context, error), onClickListener)
    }

    /**
     * Default dialog showing an message.
     * @param context context of activity.
     * @param message message to display.
     * @param onClickListener callback when clicking on button.
     * @return alert dialog.
     */
    fun getMessageAlertDialog(
        context: Context,
        message: String,
        onClickListener: AlertDialogButtonOnClickListener?
    ): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(context)

        val inflater = (context as Activity).layoutInflater
        val inflaterView = inflater.inflate(R.layout.dialog_text, null)
        alertDialogBuilder.setView(inflaterView)
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()

        val textView = inflaterView.findViewById(R.id.textView) as TextView?
        val okButton = inflaterView.findViewById(R.id.okButton) as Button?

        textView?.text = message

        okButton?.setOnClickListener {
            alertDialog.dismiss()
            onClickListener?.onClick()
        }

        return alertDialog
    }

    /**
     * Default dialog showing an message, asking for confirmation.
     * @param context context of activity.
     * @param message message to display.
     * @param okOnClickListener callback when confirming action.
     * @param cancelOnClickListener callback when canceling action.
     * @return alert dialog.
     */
    fun getConfirmAlertDialog(
        context: Context,
        message: String,
        okOnClickListener: AlertDialogButtonOnClickListener?,
        cancelOnClickListener: AlertDialogButtonOnClickListener?
    ): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(context)

        val inflater = (context as Activity).layoutInflater
        val inflaterView = inflater.inflate(R.layout.dialog_confirm, null)
        alertDialogBuilder.setView(inflaterView)
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()

        val textView = inflaterView.findViewById(R.id.textView) as TextView?
        val cancelButton = inflaterView.findViewById(R.id.cancelButton) as Button?
        val okButton = inflaterView.findViewById(R.id.okButton) as Button?

        textView?.text = message

        cancelButton?.setOnClickListener {
            alertDialog.dismiss()
            cancelOnClickListener?.onClick()
        }

        okButton?.setOnClickListener {
            alertDialog.dismiss()
            okOnClickListener?.onClick()
        }

        return alertDialog
    }

    /**
     * Check if unit is connected to the internet.
     * @param context context of activity.
     * @return unit is connected to the internet.
     */
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}