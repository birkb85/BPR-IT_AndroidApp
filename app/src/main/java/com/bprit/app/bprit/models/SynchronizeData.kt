package com.bprit.app.bprit.models

import android.content.Context
import android.content.pm.PackageManager
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import io.realm.Realm
import io.realm.RealmResults
import java.util.concurrent.CountDownLatch


/**
 * Synchronize data
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
        var error = ""

        val thread = Thread(Runnable {
            var numberOfRealmComponents = 0
            val realm = Realm.getDefaultInstance()
            val realmComponents = realm?.where(RealmComponent::class.java)
                ?.equalTo("shouldSynchronize", true)
                ?.findAll()
            realmComponents?.let { comps ->
                numberOfRealmComponents = comps.size
            }

            val number = numberOfRealmComponents
            val countDownLatch = CountDownLatch(number)

            val realmOperations = RealmOperations()
            val webservice = Webservice()
            realmComponents?.let { comps ->
                for (comp in comps) {
                    comp.typeId?.let { compTypeId ->
                        comp.id?.let { compId ->
                            webservice.deleteComponent(compTypeId, compId.toInt(), object : CallbackWebserviceResult {
                                override fun callbackCall(result: WebserviceResult) {
                                    if (result.success) {
                                        success = realmOperations.deleteComponent(compTypeId, compId.toInt())
                                    } else {
                                        success = false
                                        if (error == "") error = result.error
                                    }

                                    countDownLatch.countDown()
                                }
                            })
                        }
                    }
                }
            }
            realm.close()

            try {
                countDownLatch.await()
            } catch (e: InterruptedException) {
                success = false
//                Crashlytics.logException(e)
            }

            callback.callbackCall(success, error)
        })
        thread.start()
    }
}