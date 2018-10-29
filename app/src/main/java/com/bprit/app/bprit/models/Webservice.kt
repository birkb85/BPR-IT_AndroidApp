package com.bprit.app.bprit.models

import android.util.Log
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackWebservice
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import io.realm.Realm

import org.json.JSONException
import org.json.JSONObject
import java.io.*

import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * Handling Webservice communication
 */
class Webservice {

    /**
     * @return Orders API url
     */
    fun getApiOrdersUrl(): String = "http://bpr-orders.f66c82vmic.eu-west-1.elasticbeanstalk.com"

    /**
     * @return Customers API url
     */
    fun getApiCustomersUrl(): String = "http://bpr-customers.ysd42f9gnn.eu-west-1.elasticbeanstalk.com"

    /**
     * @return Status API url
     */
    fun getApiStatusUrl(): String = "http://bpr-status.evihmpjzgs.eu-west-1.elasticbeanstalk.com"

    /**
     * @return Storage API url
     */
    fun getApiStorageUrl(): String = "http://bpr-storage.pkm4p6b32g.eu-west-1.elasticbeanstalk.com/"

    /**
     * Webservice API communication
     * @param urlStr url for webservice to call
     * @param contentType content type for request
     * @param httpMethod http method
     * @param body body of request
     * @param callback callback for webservice response
     */
    private fun webservice(
        urlStr: String,
        contentType: String,
        httpMethod: String,
        body: String,
        callback: CallbackWebservice
    ) {
        val thread = Thread(Runnable {
            var error = ""
            var response = byteArrayOf()
            var statusCode = 0

            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection

                if (httpMethod == "POST" || httpMethod == "PUT") {
                    connection.doOutput = true
                    connection.setFixedLengthStreamingMode(body.toByteArray(charset("UTF-8")).size)
                }
                connection.doInput = true
                connection.setRequestProperty("Content-Type", contentType)

                // Set AzureAD token as authorization header
                connection.setRequestProperty("Authorization", "Bearer ${Global.azureAD?.getToken()}")

                connection.requestMethod = httpMethod
                connection.connectTimeout = 120 * 1000
                connection.readTimeout = 120 * 1000

                if (httpMethod == "POST" || httpMethod == "PUT") {
                    val dataOutputStream = DataOutputStream(connection.outputStream)
                    val bufferedWriter = BufferedWriter(OutputStreamWriter(dataOutputStream, charset("UTF-8")))
                    bufferedWriter.write(body)
                    bufferedWriter.close()
                    dataOutputStream.close()
                }

                var inputStream: InputStream? = null
                if (connection.inputStream != null) {
                    inputStream = connection.inputStream
                } else {
                    inputStream = connection.errorStream
                }
//                if (connection.responseCode == 200) {
//                    inputStream = connection.inputStream
//                } else {
//                    inputStream = connection.errorStream
//                }

                val bufferedInputStream = BufferedInputStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val readBuffer = ByteArray(1024)
                var bytesRead: Int

                bytesRead = bufferedInputStream.read(readBuffer, 0, readBuffer.size)
                while (bytesRead != -1) {
                    byteArrayOutputStream.write(readBuffer, 0, bytesRead)
                    bytesRead = bufferedInputStream.read(readBuffer, 0, readBuffer.size)
                }

//                while ((bytesRead = bufferedInputStream.read(readBuffer, 0, readBuffer.size)) != -1) {
//                    byteArrayOutputStream.write(readBuffer, 0, bytesRead)
//                }

                response = byteArrayOutputStream.toByteArray()
                bufferedInputStream.close()
                byteArrayOutputStream.close()

                statusCode = connection.responseCode

                connection.disconnect()
            } catch (e: Exception) {
//                    Crashlytics.logException(e);
                error = e.localizedMessage
            }

            callback.callbackCall(response, error, statusCode)
        })
        thread.start()
    }

    /**
     * Get component types
     * @param callback callback for result of webservice call
     */
    fun getComponentTypes(
        callback: CallbackWebserviceResult
    ) {
        val url = getApiStorageUrl() + "/component-types"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 200) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            Log.d("DEBUG", "responseString: $responseString")

                            val realm = Realm.getDefaultInstance()
                            realm.beginTransaction()
                            try {
                                // Remove old realm objects
                                realm.delete(RealmComponentType::class.java)

                                // Create new realm objects
                                val jsonArray = jsonObject.optJSONArray("componentTypes")
                                for (i in 0..(jsonArray.length() - 1)) {
                                    val componentType = jsonArray.optJSONObject(i)

                                    val dateTimeFunctions = DateTimeFunctions()

                                    val realmComponentType = RealmComponentType()
                                    realmComponentType.id = componentType.optInt("id")
                                    realmComponentType.name = componentType.optString("name")
                                    realmComponentType.created =
                                            dateTimeFunctions.getDateFromString(componentType.optString("created"))
                                    realmComponentType.modified =
                                            dateTimeFunctions.getDateFromString(componentType.optString("modified"))
                                    realmComponentType.inStorage = componentType.optInt("inStorage")
                                    realm.copyToRealm(realmComponentType)
                                }
                            } finally {
                                realm.commitTransaction()
                                realm.close()
                            }

                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }
                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * Get all components
     * @param callback callback for result of webservice call
     */
    fun getAllComponents(
        callback: CallbackWebserviceResult
    ) {
        val url = getApiStorageUrl() + "/component-types/components"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 200) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            Log.d("DEBUG", "responseString: $responseString")

                            val realm = Realm.getDefaultInstance()
                            realm.beginTransaction()
                            try {
                                // Remove old realm objects
                                realm.delete(RealmComponent::class.java)
//                                realm.delete(RealmComponentType::class.java) // BB 2018-10-29. Do not update type.

                                // Create new realm objects
                                val jsonArray = jsonObject.optJSONArray("components")
                                for (i in 0..(jsonArray.length() - 1)) {
                                    // Get json objects
                                    val component = jsonArray.optJSONObject(i)
                                    val componentType = component.optJSONObject("type")

                                    // Create new realm objects
                                    val dateTimeFunctions = DateTimeFunctions()

                                    // Create component type if it does not already exist
                                    val componentTypeId = componentType.optInt("id")
                                    val existingType =
                                        realm?.where(RealmComponentType::class.java)?.equalTo("id", componentTypeId)
                                            ?.findFirst()
                                    if (existingType == null) {
                                        val realmComponentType = RealmComponentType()
                                        realmComponentType.id = componentType.optInt("id")
                                        realmComponentType.name = componentType.optString("name")
                                        realmComponentType.created =
                                                dateTimeFunctions.getDateFromString(componentType.optString("created"))
                                        realmComponentType.modified =
                                                dateTimeFunctions.getDateFromString(componentType.optString("modified"))
                                        realmComponentType.inStorage = componentType.optInt("inStorage")
                                        realm.copyToRealm(realmComponentType)
                                    }

                                    // Create component
                                    val realmComponent = RealmComponent()
                                    realmComponent.id = component.optInt("id").toString()
                                    realmComponent.typeId = componentTypeId
                                    realmComponent.created =
                                            dateTimeFunctions.getDateFromString(component.optString("created"))
                                    realmComponent.modified =
                                            dateTimeFunctions.getDateFromString(component.optString("modified"))
                                    realmComponent.usedIn = component.optInt("usedIn")
                                    realm.copyToRealm(realmComponent)
                                }
                            } finally {
                                realm.commitTransaction()
                                realm.close()
                            }

                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }
                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * Get components for type
     * @param typeId id of component type
     * @param callback callback for result of webservice call
     */
    fun getComponentsForType(
        typeId: Int,
        callback: CallbackWebserviceResult
    ) {
        val url = getApiStorageUrl() + "/component-types/$typeId/components"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 200) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            Log.d("DEBUG", "responseString: $responseString")

                            val realm = Realm.getDefaultInstance()
                            realm.beginTransaction()
                            try {
                                // Remove old realm objects
                                // BB 2018-10-29. Do not update type.
//                                val ct = realm?.where(RealmComponentType::class.java)?.equalTo("id", typeId)?.findFirst()
//                                ct?.deleteFromRealm()

                                val realmComponents = realm?.where(RealmComponent::class.java)?.equalTo("typeId", typeId)?.findAll()
                                realmComponents?.let { components ->
                                    for (component in components) {
                                        component.deleteFromRealm()
                                    }
                                }

                                // Create new realm objects
                                val jsonArray = jsonObject.optJSONArray("components")
                                for (i in 0..(jsonArray.length() - 1)) {
                                    // Get json objects
                                    val component = jsonArray.optJSONObject(i)
                                    val componentType = component.optJSONObject("type")

                                    // Create new realm objects
                                    val dateTimeFunctions = DateTimeFunctions()

                                    // Create component type if it does not already exist
                                    val componentTypeId = componentType.optInt("id")
                                    val existingType =
                                        realm?.where(RealmComponentType::class.java)?.equalTo("id", componentTypeId)
                                            ?.findFirst()
                                    if (existingType == null) {
                                        val realmComponentType = RealmComponentType()
                                        realmComponentType.id = componentType.optInt("id")
                                        realmComponentType.name = componentType.optString("name")
                                        realmComponentType.created =
                                                dateTimeFunctions.getDateFromString(componentType.optString("created"))
                                        realmComponentType.modified =
                                                dateTimeFunctions.getDateFromString(componentType.optString("modified"))
                                        realmComponentType.inStorage = componentType.optInt("inStorage")
                                        realm.copyToRealm(realmComponentType)
                                    }

                                    // Create component
                                    val realmComponent = RealmComponent()
                                    realmComponent.id = component.optInt("id").toString()
                                    realmComponent.typeId = componentTypeId
                                    realmComponent.created =
                                            dateTimeFunctions.getDateFromString(component.optString("created"))
                                    realmComponent.modified =
                                            dateTimeFunctions.getDateFromString(component.optString("modified"))
                                    realmComponent.usedIn = component.optInt("usedIn")
                                    realm.copyToRealm(realmComponent)
                                }
                            } finally {
                                realm.commitTransaction()
                                realm.close()
                            }

                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * Delete component
     * @param typeId id of type for component
     * @param id component id
     * @param callback callback for result of webservice call
     */
    fun deleteComponent(
        typeId: Int,
        id: Int,
        callback: CallbackWebserviceResult
    ) {
        val url = getApiStorageUrl() + "/component-types/$typeId/components/$id"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "DELETE", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 202) {
                        try {
                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }
                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * Testing Orders API
     * @param callback callback for result of webservice call
     */
    fun testOrder(
        callback: CallbackWebserviceResult
    ) {
        val url = getApiOrdersUrl() + "/orders"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 200) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            Log.d("DEBUG", "responseString: $responseString")

                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }
                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * Testing Customers API
     * @param callback callback for result of webservice call
     */
    fun testCustomer(
        callback: CallbackWebserviceResult
    ) {
        val url = getApiCustomersUrl() + "/customers"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 200) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            Log.d("DEBUG", "responseString: $responseString")

                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }
                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * Testing Status API
     * @param callback callback for result of webservice call
     */
    fun testStatusTypes(
        callback: CallbackWebserviceResult
    ) {
        val url = getApiStatusUrl() + "/status-types"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {
                    result.error = error
                    result.success = false

                } else {
                    if (statusCode == 200) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            Log.d("DEBUG", "responseString: $responseString")

                            result.error = ""
                            result.success = true

                        } catch (e: Exception) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }
                    } else {
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }
}
