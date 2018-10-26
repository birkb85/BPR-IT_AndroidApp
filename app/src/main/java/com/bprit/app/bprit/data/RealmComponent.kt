package com.bprit.app.bprit.data

import io.realm.RealmObject
import java.util.*


/**
 * Realm object containing component details
 */
open class RealmComponent(
    open var id: Int? = null,
    open var type: RealmComponentType? = null,
    open var created: Date? = null,
    open var modified: Date? = null,
    open var usedIn: Int? = null,
    open var shouldSynchronize: Boolean = false
) : RealmObject() {
}