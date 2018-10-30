package com.bprit.app.bprit.data

import io.realm.RealmObject
import java.util.*


/**
 * Realm object containing component details.
 */
open class RealmComponent(
    open var id: String? = null, // BB 2018-10-27. Has to be String, because it is simulating serial number
    open var typeId: Int? = null,
    open var created: Date? = null,
    open var modified: Date? = null,
    open var usedIn: Int? = null,
    open var isDeleted: Boolean = false,
    open var shouldSynchronize: Boolean = false
) : RealmObject() {
}