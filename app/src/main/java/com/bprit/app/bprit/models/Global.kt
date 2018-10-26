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


/**
 * Global functions
 */
class Global : Application() {
    companion object {
        /**
         * Preference file key for saving to local preferences
         */
        fun getPreferenceFileKey(): String = "SharedPreferences"

        /**
         * Time interval when selecting time in spinner
         */
        fun getMinuteTimeInterval(): Int = 5
    }

//    private var context: Context? = null

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"

        // Debug realm
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // Only while developing the app!!!

        // Release realm
        // To make an migration bump "schemaVersion" 1 up and write the changes in the method "realmMigration".
        // TODO Public version has schema version 0! When next version is made public set schema version to 1 and collect migrations in "oldVersion == 0".
//        val config = RealmConfiguration.Builder().schemaVersion(0).migration(realmMigration).build()
        Realm.setDefaultConfiguration(config)
    }

    /** Controls migrations in release */
    var realmMigration: RealmMigration = RealmMigration { realm, oldVersion, newVersion ->
        // More info at: https://realm.io/docs/java/latest/#migrations
        val schema = realm.schema

        // ----------- DO NOT EDIT MIGRATIONS ABOVE THIS LINE --------------

//        if (oldVersion < 1) {
//            schema.create("RealmAzureAD")
//                .addField("idToken", String::class.java)
//        }


        // Eksempel på migration
        //            if (oldVersion == 0) {
        //                schema.create("Person")
        //                        .addField("name", String.class)
        //                        .addField("age", int.class);
        //                oldVersion++;
        //            }
        //
        //            if (oldVersion == 1) {
        //                schema.get("Person")
        //                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
        //                        .addRealmObjectField("favoriteDog", schema.get("Dog"))
        //                        .addRealmListField("dogs", schema.get("Dog"));
        //                oldVersion++;
        //            }
    }

    /**
     * Default dialog showing an error
     * @param context context of activity
     * @param message message to display in alert dialog
     * @param onClickListener callback when clicking on button
     * @return alert dialog
     */
    fun getErrorAlertDialog(
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
     * User friendly message for error
     * @param context context of activity
     * @param value http status code / error message
     * @return friendly message
     */
    fun errorMessage(
        context: Context,
        value: String
    ): String {
        var message = ""

        if (value.contains("No address associated with hostname", true)) {
            message = context.getString(R.string.dialog_errorNoAddressAssociatedWithHostName)
        }

        if (message == "") {
            when (value) {
                "" -> message = "" // OK
                "304" -> message = "$value ${context.getString(R.string.dialog_error304)}"
                "400" -> message = "$value ${context.getString(R.string.dialog_error400)}"
                "401" -> message = "$value ${context.getString(R.string.dialog_error401)}"
                "404" -> message = "$value ${context.getString(R.string.dialog_error404)}"
                "500" -> message = "$value ${context.getString(R.string.dialog_error500)}"
                "503" -> message = "$value ${context.getString(R.string.dialog_error503)}"
                else -> message = context.getString(R.string.dialog_errorDefault)
            }
        }

        message = ("${context.getString(R.string.dialog_error)} $message").trim()

        return message
    }
}