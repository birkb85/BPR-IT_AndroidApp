package com.bprit.app.bprit.data

import io.realm.RealmObject


/**
 * AzureAD Realm object containing AzureAD details
 */
open class RealmAzureAD(
    open var idToken: String = "",
    open var odatacontext: String = "",
    open var id: String = "",
    open var businessPhones: String = "",
    open var displayName: String = "",
    open var givenName: String = "",
    open var jobTitle: String = "",
    open var mail: String = "",
    open var mobilePhone: String = "",
    open var officeLocation: String = "",
    open var preferredLanguage: String = "",
    open var surname: String = "",
    open var userPrincipalName: String = "") : RealmObject() {
}