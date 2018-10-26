package com.bprit.app.bprit.model

import com.bprit.app.bprit.data.RealmAzureAD
import io.realm.Realm
import java.util.*
import java.text.ParseException
import java.text.SimpleDateFormat


/**
 * Get information about user logged in
 */
class UserFunctions {

    /**
     * @return displayname for user logged in
     */
    fun getDisplayName(): String {
        var displayName = ""

        val realm = Realm.getDefaultInstance()
        val realmAzureAD: RealmAzureAD? = realm.where(RealmAzureAD::class.java).findFirst()
        realmAzureAD?.let { obj ->
            displayName = obj.displayName
        }
        realm.close()

        return displayName
    }
}