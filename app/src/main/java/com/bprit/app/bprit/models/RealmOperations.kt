package com.bprit.app.bprit.models

import com.bprit.app.bprit.data.RealmComponent
import io.realm.Realm
import java.lang.Exception

/**
 * Realm operations
 */
class RealmOperations {
    /**
     * Mark component as deleted and that it should synchronize
     * @param typeId type id of component
     * @param id id of component
     */
    fun syncDeleteComponent(typeId: Int, id: Int) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        try {
            val realmComponent =
                realm?.where(RealmComponent::class.java)
                    ?.equalTo("id", id.toString())
                    ?.equalTo("typeId", typeId)
                    ?.equalTo("isDeleted", false)?.findFirst()
            realmComponent?.let { component ->
                component.isDeleted = true
                component.shouldSynchronize = true
            }
        } finally {
            realm.commitTransaction()
            realm.close()
        }
    }

    /**
     * Delete component from realm
     * @param typeId type id of component
     * @param id id of component
     * @return if component is deleted
     */
    fun deleteComponent(typeId: Int, id: Int): Boolean {
        var deleted = false

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        try {
            val realmComponent =
                realm?.where(RealmComponent::class.java)
                    ?.equalTo("typeId", typeId)
                    ?.equalTo("id", id.toString())
                    ?.findFirst()
            realmComponent?.deleteFromRealm()
            deleted = true
        } finally {
            realm.commitTransaction()
            realm.close()
        }

        return deleted
    }

    /**
     * Check if component is deleted
     * @param typeId type id of component
     * @param id id of component
     * @return if component is deleted
     */
    fun isComponentDeleted(typeId: Int, id: Int): Boolean {
        var isDeleted = true

        val realm = Realm.getDefaultInstance()
        val realmComponent = realm?.where(RealmComponent::class.java)
            ?.equalTo("id", id.toString())
            ?.equalTo("typeId", typeId)
            ?.equalTo("isDeleted", false)?.findFirst()
        realmComponent?.let {
            isDeleted = false
        }
        realm.close()

        return isDeleted
    }
}