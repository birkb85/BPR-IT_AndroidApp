package com.bprit.app.bprit.models

import android.content.Context
import android.content.pm.PackageManager

/**
 * Get information about application
 */
class ApplicationInformation {

    /**
     * Get version name of application
     * @param context context of activity / fragment
     * @return The version name
     */
    fun getVersion(context : Context): String {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
//            Global.showExceptionAlertDialog(e, activity, alertDialogs)
//            Crashlytics.logException(e)
        }
        return ""
    }
}