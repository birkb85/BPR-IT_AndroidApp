package com.bprit.app.bprit.interfaces

/**
 * TODO XML description
 */
interface CallbackWebservice {
    fun callbackCall(response: ByteArray, error: String, statusCode: Int)
}