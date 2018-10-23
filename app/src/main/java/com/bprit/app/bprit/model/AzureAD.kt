package com.bprit.app.bprit.model

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.microsoft.identity.client.*
import org.json.JSONObject
import java.util.HashMap

/**
 * TODO XML description
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/tutorial-v2-android
 */
class AzureAD(private val activity: FragmentActivity) {

    private val CLIENT_ID = "ea3501cb-f257-4d92-96e5-1f534d4343d1" //"[Enter the application Id here]";
    private val SCOPES = arrayOf("https://graph.microsoft.com/User.Read")
    private val MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me"

    /* UI & Debugging Variables */
    private val TAG = "DEBUG" //MainActivity.class.getSimpleName();
//    internal var callGraphButton: Button // TODO BB 2018-10-23.
//    internal var signOutButton: Button // TODO BB 2018-10-23.

    /* Azure AD Variables */
    private var sampleApp: PublicClientApplication? = null
    private var authResult: AuthenticationResult? = null

    /**
     * Initialise AzureAD connection
     */
    init {
//        callGraphButton = findViewById(R.id.callGraph) as Button // TODO BB 2018-10-23.
//        signOutButton = findViewById(R.id.clearCache) as Button // TODO BB 2018-10-23.

//        callGraphButton.setOnClickListener { onCallGraphClicked() } // TODO BB 2018-10-23.

//        signOutButton.setOnClickListener { onSignOutClicked() } // TODO BB 2018-10-23.

        /* Configure your sample app and save state for this activity */
        sampleApp = null
        if (sampleApp == null) {
            sampleApp = PublicClientApplication(
                activity.applicationContext,
                CLIENT_ID
            )
        }

        // BB 2018-10-23. Do not log in automatically
//        /* Attempt to get a user and acquireTokenSilent
//         * If this fails we do an interactive request
//         */
//        var users: List<User>? = null
//
//        try {
//            users = sampleApp?.users
//
//            if (users != null && users.size == 1) {
//                /* We have 1 user */
//
//                sampleApp?.acquireTokenSilentAsync(SCOPES, users[0], getAuthSilentCallback())
//            } else {
//                /* We have no user */
//
//                /* Let's do an interactive request */
//                sampleApp?.acquireToken(activity, SCOPES, getAuthInteractiveCallback())
//            }
//        } catch (e: MsalClientException) {
//            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString())
//
//        } catch (e: IndexOutOfBoundsException) {
//            Log.d(TAG, "User at this position does not exist: " + e.toString())
//        }
    }

    //
    // App callbacks for MSAL
    // ======================
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case
    //

    /* Callback method for acquireTokenSilent calls
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private fun getAuthSilentCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated")

                /* Store the authResult */
                authResult = authenticationResult

                /* call graph */
                callGraphAPI()

                /* update the UI to post call Graph state */
                updateSuccessUI()
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString())

                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception is MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }

            override fun onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.")
            }
        }
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private fun getAuthInteractiveCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated")
                Log.d(TAG, "ID Token: " + authenticationResult.idToken)

                /* Store the auth result */
                authResult = authenticationResult

                /* call Graph */
                callGraphAPI()

                /* update the UI to post call Graph state */
                updateSuccessUI()
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString())

                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            override fun onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.")
            }
        }
    }

    /* Set the UI for successful token acquisition data */
    private fun updateSuccessUI() {
//        callGraphButton.visibility = View.INVISIBLE // TODO BB 2018-10-23.
//        signOutButton.visibility = View.VISIBLE // TODO BB 2018-10-23.
//        findViewById(R.id.welcome).setVisibility(View.VISIBLE) // TODO BB 2018-10-23.
//        (findViewById(R.id.welcome) as TextView).text = "Welcome, " + authResult.getUser().name // TODO BB 2018-10-23.
//        findViewById(R.id.graphData).setVisibility(View.VISIBLE) // TODO BB 2018-10-23.
    }

    /**
     * Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    fun onCallGraphClicked() {
        sampleApp?.acquireToken(activity, SCOPES, getAuthInteractiveCallback())
    }

    /* Handles the redirect from the System Browser */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        sampleApp?.handleInteractiveRequestRedirect(requestCode, resultCode, data)
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private fun callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph")

        /* Make sure we have a token to send to graph */
        if (authResult?.accessToken == null) {
            return
        }

        val queue = Volley.newRequestQueue(activity) // TODO BB 2018-10-23. Old 'This'. Test that this works as fine...
        val parameters = JSONObject()

        try {
            parameters.put("key", "value")
        } catch (e: Exception) {
            Log.d(TAG, "Failed to put parameters: " + e.toString())
        }

        val request = object : JsonObjectRequest(
            Request.Method.GET, MSGRAPH_URL,
            parameters, Response.Listener { response ->
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString())

                updateGraphUI(response)
            }, Response.ErrorListener { error -> Log.d(TAG, "Error: " + error.toString()) }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + authResult?.accessToken
                return headers
            }
        }

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString())

        request.retryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(request)
    }

    /* Sets the Graph response */
    private fun updateGraphUI(graphResponse: JSONObject) {
//        val graphText = findViewById(R.id.graphData) as TextView // TODO BB 2018-10-23.
//        graphText.text = graphResponse.toString() // TODO BB 2018-10-23.
    }

    /**
     * Clears a user's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     */
    fun onSignOutClicked() {

        /* Attempt to get a user and remove their cookies from cache */
        try {
            val users: List<User>? = sampleApp?.users

            if (users == null) {
                /* We have no users */

            } else if (users.size == 1) {
                /* We have 1 user */
                /* Remove from token cache */
                sampleApp?.remove(users[0])
                updateSignedOutUI()

            } else {
                /* We have multiple users */
                for (i in users.indices) {
                    sampleApp?.remove(users[i])
                }
            }

            // TODO BB 2018-10-23. Old 'getBaseContext()'. Test that this works as fine...
            Toast.makeText(activity, "Signed Out!", Toast.LENGTH_SHORT).show()

        } catch (e: MsalClientException) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString())

        } catch (e: IndexOutOfBoundsException) {
            Log.d(TAG, "User at this position does not exist: " + e.toString())
        }

    }

    /* Set the UI for signed-out user */
    private fun updateSignedOutUI() {
//        callGraphButton.visibility = View.VISIBLE // TODO BB 2018-10-23.
//        signOutButton.visibility = View.INVISIBLE // TODO BB 2018-10-23.
//        findViewById(R.id.welcome).setVisibility(View.INVISIBLE) // TODO BB 2018-10-23.
//        findViewById(R.id.graphData).setVisibility(View.INVISIBLE) // TODO BB 2018-10-23.
//        (findViewById(R.id.graphData) as TextView).text = "No Data" // TODO BB 2018-10-23.
    }
}