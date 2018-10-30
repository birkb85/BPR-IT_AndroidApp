package com.bprit.app.bprit.interfaces

import com.bprit.app.bprit.data.WebserviceResult

/**
 * Result of Webservice call.
 */
interface CallbackWebserviceResult {
    /**
     * Result of Webservice call.
     * @param result contains result of webservice call.
     */
    fun callbackCall(result: WebserviceResult)
}