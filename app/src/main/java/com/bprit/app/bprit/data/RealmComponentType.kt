package com.bprit.app.bprit.data

import io.realm.RealmObject
import java.util.*


/**
 * Realm object containing component type details
 */
open class RealmComponentType(
    open var id: Int? = null,
    open var name: String? = null,
    open var created: Date? = null,
    open var modified: Date? = null,
    open var inStorage: Int? = null
) : RealmObject() {
}