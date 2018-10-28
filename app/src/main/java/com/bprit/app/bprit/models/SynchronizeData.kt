package com.bprit.app.bprit.models

import android.content.Context
import android.content.pm.PackageManager
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import io.realm.Realm
import io.realm.RealmResults

/**
 * Realm operations
 */
class SynchronizeData {

    /**
     * Check if application should sync data
     * @return if application should synchronize data
     */
    fun shouldSynchronizeData(): Boolean {
        var shouldSync = false

        val realm = Realm.getDefaultInstance()

        // Check component type
        val realmComponentTypeRealmResults =
            realm?.where(RealmComponentType::class.java)?.equalTo("shouldSynchronize", true)?.findAll()
        realmComponentTypeRealmResults?.let { results ->
            if (results.size > 0) shouldSync = true
        }

        if (!shouldSync) {
            // Check component
            val realmComponentRealmResults =
                realm?.where(RealmComponent::class.java)?.equalTo("shouldSynchronize", true)?.findAll()
            realmComponentRealmResults?.let { results ->
                if (results.size > 0) shouldSync = true
            }
        }

        realm.close()

        return shouldSync
    }

    /**
     * Synchronize data
     * @param callback callback called when finished synchronizing data
     */
    fun synchronizeData(callback: CallbackSynchronizeData) {
        var success = true

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        try {
            // Sync component types
            val realmComponentTypeRealmResults =
                realm?.where(RealmComponentType::class.java)?.equalTo("shouldSynchronize", true)?.findAll()
            realmComponentTypeRealmResults?.let { results ->
                for (componentType in results) {
                    // TODO BB 2018-10-27. Component types are not synched at the moment..
                    componentType.shouldSynchronize = false
                }
            }

            // Sync components
            val realmComponentRealmResults =
                realm?.where(RealmComponent::class.java)?.equalTo("shouldSynchronize", true)?.findAll()
            realmComponentRealmResults?.let { results ->
                for (component in results) {
                    // TODO BB 2018-10-27. Components are not synched at the moment..
                    component.shouldSynchronize = false
                }
            }
        } finally {
            realm.commitTransaction()
            realm.close()
        }

        callback.callbackCall(success)
    }
}