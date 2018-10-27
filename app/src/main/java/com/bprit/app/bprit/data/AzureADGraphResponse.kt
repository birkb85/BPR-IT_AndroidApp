package com.bprit.app.bprit.data

import io.realm.RealmObject
import org.json.JSONArray


/**
 * AzureAD Realm object containing AzureAD details
 */
class AzureADGraphResponse(
    var odatacontext: String = "",
    var id: String = "",
    var businessPhones: JSONArray = JSONArray(),
    var displayName: String = "",
    var givenName: String = "",
    var jobTitle: String = "",
    var mail: String = "",
    var officeLocation: String = "",
    var mobilePhone: String = "",
    var preferredLanguage: String = "",
    var surname: String = "",
    var userPrincipalName: String = "") {
}