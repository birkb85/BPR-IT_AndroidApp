package com.bprit.app.bprit.model

import android.net.Uri
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
 * TODO XML description
 */
class Webservice {

    /**
     * TODO XML description
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
//                if (sessionGUID != "") { // TODO BB 2018-10-22. If I should set header.
//                    connection.setRequestProperty("CustomHeader", sessionGUID)
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
     * TODO XML description
     */
    fun login(
        username: String,
        password: String,
        callback: CallbackWebserviceResult
    ) {
        val url = Global.getApiOrdersUrl() + "/login" + // TODO
                "?Username=" + Uri.encode(username) +
                "&Password=" + Uri.encode(password)
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
     * TODO XML description
     */
    fun logout(callback: CallbackWebserviceResult) {
        val url = Global.getApiOrdersUrl() + "/logout"
        val contentType = "application/json; charset=utf-8"
        val body = ""

        webservice(url, contentType, "GET", /*sessionGUID,*/ body, object : CallbackWebservice {
            override fun callbackCall(response: ByteArray, error: String, statusCode: Int) {
                val result = WebserviceResult()
                if (error != "") {

                    //                    Log.e(TAG, "Webservice error: " + error);

                    result.error = error
                    result.success = false
                } else {
                    if (statusCode == 200) {
                        result.error = ""
                        result.success = true
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
                        result.error = statusCode.toString()
                        result.success = false
                    }
                }

                callback.callbackCall(result)
            }
        })
    }

    /**
     * TODO XML description
     */
    fun testOrder(
        callback: CallbackWebserviceResult
    ) {
        val url = Global.getApiOrdersUrl() + "/orders"
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
     * TODO XML description
     */
    fun testCustomer(
        callback: CallbackWebserviceResult
    ) {
        val url = Global.getApiCustomersUrl() + "/customers"
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
