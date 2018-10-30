package com.bprit.app.bprit.models

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bprit.app.bprit.interfaces.CallbackAzureAD
import com.microsoft.identity.client.*
import org.json.JSONObject
import java.util.HashMap
import com.bprit.app.bprit.data.AzureADGraphResponse

// TODO BB 2018-10-25. App gets a token when logging in. This token should be renewed if it expires. Implement this.

/**
 * Handles AzureAD communication.
 * Example: https://docs.microsoft.com/en-us/azure/active-directory/develop/tutorial-v2-android.
 * Heavily modified for our custom use.
 */
class AzureAD {

    private val CLIENT_ID = "ea3501cb-f257-4d92-96e5-1f534d4343d1" //"[Enter the application Id here]";
    private val SCOPES = arrayOf("https://graph.microsoft.com/User.Read")
    private val MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me"

    /* UI & Debugging Variables */
    private val TAG = "DEBUG"

    /* Azure AD Variables */
    private var azureADApp: PublicClientApplication? = null
    private var authResult: AuthenticationResult? = null
    private var azureADGraphResponse: AzureADGraphResponse? = null

    /**
     * Sign in to Azure AD.
     * @param activity context.
     * @param callback callback to call when finished.
     */
    fun signIn(activity: FragmentActivity, callback: CallbackAzureAD) {
        /* Configure your sample app and save state for this activity */
        azureADApp = null
        if (azureADApp == null) {
            azureADApp = PublicClientApplication(
                activity.applicationContext,
                CLIENT_ID
            )
        }

        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        try {
            azureADApp?.let { app ->
                val users = app.users

                if (users != null && users.size == 1) {
                    /* We have 1 user */

                    azureADApp?.acquireTokenSilentAsync(SCOPES, users[0], getAuthSilentCallback(activity, callback))
                } else {
                    /* We have no user */

                    /* Let's do an interactive request */
                    azureADApp?.acquireToken(activity, SCOPES, getAuthInteractiveCallback(activity, callback))
                }
            }
        } catch (e: MsalClientException) {
//            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString())
            callCallback(callback,false, false)
        } catch (e: IndexOutOfBoundsException) {
//            Log.d(TAG, "User at this position does not exist: " + e.toString())
            callCallback(callback,false, false)
        }
    }

    /**
     * Sign out of Azure AD.
     * @param activity context.
     * @param callback callback to call when finished.
     */
    fun signOut(activity: FragmentActivity, callback: CallbackAzureAD) {
        /* Configure your sample app and save state for this activity */
        azureADApp = null
        if (azureADApp == null) {
            azureADApp = PublicClientApplication(
                activity.applicationContext,
                CLIENT_ID
            )
        }

        /* Attempt to get a user and remove their cookies from cache */
        try {
            azureADApp?.let { app ->
                val users = app.users

                if (users == null) {
                    /* We have no users */

                } else if (users.size == 1) {
                    /* We have 1 user */
                    /* Remove from token cache */
                    app.remove(users[0])

                } else {
                    /* We have multiple users */
                    for (i in users.indices) {
                        app.remove(users[i])
                    }
                }
            }

            callCallback(callback,true, false)

        } catch (e: MsalClientException) {
//            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString())
            callCallback(callback,false, true)

        } catch (e: IndexOutOfBoundsException) {
//            Log.d(TAG, "User at this position does not exist: " + e.toString())
            callCallback(callback,false, true)
        }
    }

    /**
     * Get Azure AD token.
     * @return token.
     */
    fun getToken() : String? {
        return authResult?.idToken
    }

    /**
     * Get Azure AD user display name.
     * @return display name.
     */
    fun getDisplayName() : String? {
        return azureADGraphResponse?.displayName
    }

    /**
     * Callback used for silent request. If succeeds we use the access token to call the Microsoft Graph.
     * @param activity context.
     * @param callback callback to call when finished.
     * @return callback with result.
     */
    private fun getAuthSilentCallback(activity: FragmentActivity, callback: CallbackAzureAD): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated")
                Log.d(TAG, "ID Token: " + authenticationResult.idToken)
                Log.d(TAG, "Expires On: " + authenticationResult.expiresOn)

                /* Store the authResult */
                authResult = authenticationResult

                /* call graph */
                callGraphAPI(activity, callback)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
//                Log.d(TAG, "Authentication failed: " + exception.toString())

                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception is MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }

                callCallback(callback,false, false)
            }

            override fun onCancel() {
                /* User cancelled the authentication */
//                Log.d(TAG, "User cancelled login.")

                callCallback(callback,false, false)
            }
        }
    }

    /**
     * Callback used for interactive request. If succeeds we use the access token to call the Microsoft Graph.
     * @param activity context.
     * @param callback callback to call when finished.
     * @return callback with result.
     */
    private fun getAuthInteractiveCallback(activity: FragmentActivity, callback: CallbackAzureAD): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated")
                Log.d(TAG, "ID Token: " + authenticationResult.idToken)
                Log.d(TAG, "Expires On: " + authenticationResult.expiresOn)

                /* Store the auth result */
                authResult = authenticationResult

                /* call Graph */
                callGraphAPI(activity, callback)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
//                Log.d(TAG, "Authentication failed: " + exception.toString())

                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }

                callCallback(callback,false, false)
            }

            override fun onCancel() {
                /* User cancelled the authentication */
//                Log.d(TAG, "User cancelled login.")

                callCallback(callback,false, false)
            }
        }
    }

    /**
     * Handles the redirect from the System Browser.
     * @param requestCode request code of activity result.
     * @param resultCode result code of activity result.
     * @param data data of activity result.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        azureADApp?.handleInteractiveRequestRedirect(requestCode, resultCode, data)
    }

    /**
     * Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token.
     * @param activity context.
     * @param callback the callback to call when finished.
     */
    private fun callGraphAPI(activity: FragmentActivity, callback: CallbackAzureAD) {
//        Log.d(TAG, "Starting volley request to graph")

        /* Make sure we have a token to send to graph */
        if (authResult?.accessToken == null) {
            return
        }

        val queue = Volley.newRequestQueue(activity) // BB 2018-10-23. Old 'This'.
        val parameters = JSONObject()

        try {
            parameters.put("key", "value")
        } catch (e: Exception) {
//            Log.d(TAG, "Failed to put parameters: " + e.toString())
        }

        val request = object : JsonObjectRequest(
            Request.Method.GET, MSGRAPH_URL,
            parameters, Response.Listener { response ->
                /* Successfully called graph, process data and send to UI */
//                Log.d(TAG, "Response: " + response.toString())

                updateGraphUI(response, callback)
            }, Response.ErrorListener { error ->
//                Log.d(TAG, "Error: " + error.toString())
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + authResult?.accessToken
                return headers
            }
        }

//        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString())

        request.retryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(request)
    }

    /**
     * Sets the Graph response, and call callback that user is signed in successfully.
     * @param graphResponse the response from graph.
     * @param callback the callback to call when finished.
     */
    private fun updateGraphUI(graphResponse: JSONObject, callback: CallbackAzureAD) {
//        Log.d(TAG, "updateGraphUI: " + graphResponse.toString())

        // Save data to graph response
        azureADGraphResponse = AzureADGraphResponse()
        azureADGraphResponse?.odatacontext = graphResponse.optString("@odata.context")
        azureADGraphResponse?.id = graphResponse.optString("id")
        azureADGraphResponse?.businessPhones = graphResponse.optJSONArray("businessPhones")
        azureADGraphResponse?.displayName = graphResponse.optString("displayName")
        azureADGraphResponse?.givenName = graphResponse.optString("givenName")
        azureADGraphResponse?.jobTitle = graphResponse.optString("jobTitle")
        azureADGraphResponse?.mail = graphResponse.optString("mail")
        azureADGraphResponse?.mobilePhone = graphResponse.optString("mobilePhone")
        azureADGraphResponse?.officeLocation = graphResponse.optString("officeLocation")
        azureADGraphResponse?.preferredLanguage = graphResponse.optString("preferredLanguage")
        azureADGraphResponse?.surname = graphResponse.optString("surname")
        azureADGraphResponse?.userPrincipalName = graphResponse.optString("userPrincipalName")

        callCallback(callback,true, true)
    }

    /**
     * Call callback with parameters, and set global variable isSignedIn.
     * @param callback callback to call.
     * @param success if is successful.
     * @param isSignedIn if is signed in.
     */
    private fun callCallback(callback: CallbackAzureAD, success: Boolean, isSignedIn: Boolean) {
        Global.isSignedIn = isSignedIn
        callback.callbackCall(success, isSignedIn)
    }
}