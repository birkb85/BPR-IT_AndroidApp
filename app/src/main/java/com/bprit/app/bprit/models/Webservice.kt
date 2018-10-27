package com.bprit.app.bprit.models

import android.util.Log
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackWebservice
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult

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
     * @return Status API url
     */
    fun getApiStorageUrl(): String = "http://bpr-storage.5xq2m7ipib.eu-west-1.elasticbeanstalk.com"

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
//                var idToken = ""
//                val realm = Realm.getDefaultInstance()
//                val realmAzureAD: AzureADGraphResponse? = realm.where(AzureADGraphResponse::class.java).findFirst()
//                realmAzureAD?.let { obj ->
//                    idToken = obj.idToken
//                }
//                realm.close()
//                if (idToken != "") {
//                    connection.setRequestProperty("Authorization", "Bearer $idToken")
//                }

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
                if (connection.responseCode == 200) {
                    inputStream = connection.inputStream
                } else {
                    inputStream = connection.errorStream
                }

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

                            Log.d("DEBUG", "responseString: " + responseString)

//                            login.pin = jsonObject.optBoolean("pin")
//                            login.sessionGUID = jsonObject.optString("sessionguid")

//                            login.userData = WebserviceLoginUserData()
//                            if (jsonObject.opt("userdata") != null) {
//                                val jsonUserData = JSONObject(jsonObject.opt("userdata").toString())
//                                login.userData.user_id = jsonUserData.optString("user_id")
//                                login.userData.username = jsonUserData.optString("username")
//                                login.userData.role = jsonUserData.optLong("role")
//                                //                                Log.d("Debug", "login.userData.role: " + login.userData.role);
//                                login.userData.required_app_version = jsonUserData.optString("required_app_version")
//                                login.userData.default_location = jsonUserData.optString("default_location")
//                            }

                            result.error = ""
                            result.success = true

                        } catch (e: JSONException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false

                        } catch (e: UnsupportedEncodingException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else if (statusCode == 403) {
                        result.error = "403"
                        result.success = false

                    } else if (statusCode == 500) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            result.error = jsonObject.optString("statuscode")
                            result.success = false

                        } catch (e: JSONException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false

                        } catch (e: UnsupportedEncodingException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else {
                        //                        Log.e(TAG, "Webservice communication error: " + statusCode);

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

                            Log.d("DEBUG", "responseString: " + responseString)

//                            login.pin = jsonObject.optBoolean("pin")
//                            login.sessionGUID = jsonObject.optString("sessionguid")

//                            login.userData = WebserviceLoginUserData()
//                            if (jsonObject.opt("userdata") != null) {
//                                val jsonUserData = JSONObject(jsonObject.opt("userdata").toString())
//                                login.userData.user_id = jsonUserData.optString("user_id")
//                                login.userData.username = jsonUserData.optString("username")
//                                login.userData.role = jsonUserData.optLong("role")
//                                //                                Log.d("Debug", "login.userData.role: " + login.userData.role);
//                                login.userData.required_app_version = jsonUserData.optString("required_app_version")
//                                login.userData.default_location = jsonUserData.optString("default_location")
//                            }

                            result.error = ""
                            result.success = true

                        } catch (e: JSONException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false

                        } catch (e: UnsupportedEncodingException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else if (statusCode == 403) {
                        result.error = "403"
                        result.success = false

                    } else if (statusCode == 500) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            result.error = jsonObject.optString("statuscode")
                            result.success = false

                        } catch (e: JSONException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false

                        } catch (e: UnsupportedEncodingException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else {
                        //                        Log.e(TAG, "Webservice communication error: " + statusCode);

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

                            Log.d("DEBUG", "responseString: " + responseString)

//                            login.pin = jsonObject.optBoolean("pin")
//                            login.sessionGUID = jsonObject.optString("sessionguid")

//                            login.userData = WebserviceLoginUserData()
//                            if (jsonObject.opt("userdata") != null) {
//                                val jsonUserData = JSONObject(jsonObject.opt("userdata").toString())
//                                login.userData.user_id = jsonUserData.optString("user_id")
//                                login.userData.username = jsonUserData.optString("username")
//                                login.userData.role = jsonUserData.optLong("role")
//                                //                                Log.d("Debug", "login.userData.role: " + login.userData.role);
//                                login.userData.required_app_version = jsonUserData.optString("required_app_version")
//                                login.userData.default_location = jsonUserData.optString("default_location")
//                            }

                            result.error = ""
                            result.success = true

                        } catch (e: JSONException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false

                        } catch (e: UnsupportedEncodingException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else if (statusCode == 403) {
                        result.error = "403"
                        result.success = false

                    } else if (statusCode == 500) {
                        try {
                            val responseString = String(response, Charset.forName("UTF-8"))
                            val jsonObject = JSONObject(responseString)

                            result.error = jsonObject.optString("statuscode")
                            result.success = false

                        } catch (e: JSONException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false

                        } catch (e: UnsupportedEncodingException) {
//                            Crashlytics.logException(e)
                            if (result.error.equals("")) result.error = e.localizedMessage
                            result.success = false
                        }

                    } else {
                        //                        Log.e(TAG, "Webservice communication error: " + statusCode);

                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }
}
