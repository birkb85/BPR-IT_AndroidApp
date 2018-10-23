package com.bprit.app.bprit.model

import android.app.Application
import android.content.Context

/**
 * TODO XML description
 */
class Global : Application()
{
    companion object {
        /**
         * TODO XML description
         */
        fun getPreferenceFileKey() : String = "SharedPreferences"

        /**
         * TODO XML description
         */
        fun getApiOrdersUrl() : String = "http://bpr-orders.f66c82vmic.eu-west-1.elasticbeanstalk.com"

        /**
         * TODO XML description
         */
        fun getApiCustomersUrl() : String = "http://bpr-customers.ysd42f9gnn.eu-west-1.elasticbeanstalk.com/"

        /**
         * TODO XML description
         */
        fun getMinuteTimeInterval() : Int = 5
    }

    private var context: Context? = null

    override fun onCreate() {
        super.onCreate()

        // TODO Initialise Realm
    }
}