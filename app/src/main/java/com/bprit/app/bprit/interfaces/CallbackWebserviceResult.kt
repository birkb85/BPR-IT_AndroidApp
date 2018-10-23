package com.bprit.app.bprit.interfaces

import com.bprit.app.bprit.data.WebserviceResult

/**
 * TODO XML description
 */
interface CallbackWebserviceResult {
    fun callbackCall(result: WebserviceResult)
}