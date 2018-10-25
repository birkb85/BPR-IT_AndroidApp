package com.bprit.app.bprit.interfaces

/**
 * AzureAD class callback
 */
interface CallbackAzureAD {
    /**
     * AzureAD class callback
     * @param success if azureAD function was successful
     * @param isSignedIn if user is signed in
     */
    fun callbackCall(success: Boolean, isSignedIn: Boolean)
}