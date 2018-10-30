package com.bprit.app.bprit.interfaces

/**
 * SynchronizeData class callback.
 */
interface CallbackSynchronizeData {
    /**
     * SynchronizeData class callback.
     * @param success if data was synchronized.
     * @param error http error code / error message.
     */
    fun callbackCall(success: Boolean, error: String)
}