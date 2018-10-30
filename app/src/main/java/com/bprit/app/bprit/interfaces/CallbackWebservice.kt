package com.bprit.app.bprit.interfaces

/**
 * Webservice callback used when specific webservice call is contacting API thorugh webservice.
 */
interface CallbackWebservice {
    /**
     * Webservice callback used when specific webservice call is contacting API thorugh webservice.
     * @param response containing response of webservice call.
     * @param error containg error text if any.
     * @param statusCode http status code.
     */
    fun callbackCall(response: ByteArray, error: String, statusCode: Int)
}