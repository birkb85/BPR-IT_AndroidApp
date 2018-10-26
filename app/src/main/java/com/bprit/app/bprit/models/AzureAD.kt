package com.bprit.app.bprit.models

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bprit.app.bprit.interfaces.CallbackAzureAD
import com.microsoft.identity.client.*
import org.json.JSONObject
import java.util.HashMap
import com.bprit.app.bprit.data.RealmAzureAD
import io.realm.Realm

// TODO BB 2018-10-25. App gets a token when logging in. This token should be renewed if it expires. Implement this.

/**
 * Handling AzureAD communication
 * Example: https://docs.microsoft.com/en-us/azure/active-directory/develop/tutorial-v2-android
 * @param activity context of activity
 */
class AzureAD(private var activity: FragmentActivity) {

    private var callback: CallbackAzureAD? = null

    private val CLIENT_ID = "ea3501cb-f257-4d92-96e5-1f534d4343d1" //"[Enter the application Id here]";
    private val SCOPES = arrayOf("https://graph.microsoft.com/User.Read")
    private val MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me"

    /* UI & Debugging Variables */
    private val TAG = "DEBUG" //MainActivity.class.getSimpleName();
//    internal var callGraphButton: Button
//    internal var signOutButton: Button

    /* Azure AD Variables */
    private var sampleApp: PublicClientApplication? = null
    private var authResult: AuthenticationResult? = null

    /**
     * Initialise AzureAD connection
     */
    init {
//        callGraphButton = findViewById(R.id.callGraph) as Button
//        signOutButton = findViewById(R.id.clearCache) as Button

//        callGraphButton.setOnClickListener { onCallGraphClicked() }

//        signOutButton.setOnClickListener { onSignOutClicked() }

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

    /**
     * Set activity when activity is created
     * @param activity context of activity
     */
    fun setActivity(activity: FragmentActivity) {
        this.activity = activity
    }

    /**
     * Set callback when activity is created
     * @param callback callback for Azure AD calls
     */
    fun setCallback(callback: CallbackAzureAD) {
        this.callback = callback
    }

    //
    // App callbacks for MSAL
    // ======================
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case
    //

    /**
     * Callback method for acquireTokenSilent calls
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private fun getAuthSilentCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                /* Successfully got a token, call Graph now */
//                Log.d(TAG, "Successfully authenticated")

                // Save Token to Realm
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                try {
                    // Remove old object from Realm
                    realm.delete(RealmAzureAD::class.java)

                    // Create new object in Realm
                    val realmAzureAD = RealmAzureAD()
                    realmAzureAD.idToken = authenticationResult.idToken
                    realm.copyToRealm(realmAzureAD)
                } finally {
                    realm.commitTransaction()
                    realm.close()
                }

                /* Store the authResult */
                authResult = authenticationResult

                /* call graph */
                callGraphAPI()

                /* update the UI to post call Graph state */
                updateSuccessUI()
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

                // Remove info from Realm
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                try {
                    // Remove old object from Realm
                    realm.delete(RealmAzureAD::class.java)
                } finally {
                    realm.commitTransaction()
                    realm.close()
                }

                callback?.callbackCall(false, false)
            }

            override fun onCancel() {
                /* User cancelled the authentication */
//                Log.d(TAG, "User cancelled login.")

                // Remove info from Realm
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                try {
                    // Remove old object from Realm
                    realm.delete(RealmAzureAD::class.java)
                } finally {
                    realm.commitTransaction()
                    realm.close()
                }

                callback?.callbackCall(false, false)
            }
        }
    }

    /**
     * Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private fun getAuthInteractiveCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                /* Successfully got a token, call graph now */
//                Log.d(TAG, "Successfully authenticated")
//                Log.d(TAG, "ID Token: " + authenticationResult.idToken)
//                Log.d(TAG, "Expires On: " + authenticationResult.expiresOn)

                // Save Token to Realm
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                try {
                    // Remove old object from Realm
                    realm.delete(RealmAzureAD::class.java)

                    // Create new object in Realm
                    val realmAzureAD = RealmAzureAD()
                    realmAzureAD.idToken = authenticationResult.idToken
                    realm.copyToRealm(realmAzureAD)
                } finally {
                    realm.commitTransaction()
                    realm.close()
                }

                /* Store the auth result */
                authResult = authenticationResult

                /* call Graph */
                callGraphAPI()

                /* update the UI to post call Graph state */
                updateSuccessUI()
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
//                Log.d(TAG, "Authentication failed: " + exception.toString())

                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }

                // Remove info from Realm
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                try {
                    // Remove old object from Realm
                    realm.delete(RealmAzureAD::class.java)
                } finally {
                    realm.commitTransaction()
                    realm.close()
                }

                callback?.callbackCall(false, false)
            }

            override fun onCancel() {
                /* User cancelled the authentication */
//                Log.d(TAG, "User cancelled login.")

                // Remove info from Realm
                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                try {
                    // Remove old object from Realm
                    realm.delete(RealmAzureAD::class.java)
                } finally {
                    realm.commitTransaction()
                    realm.close()
                }

                callback?.callbackCall(false, false)
            }
        }
    }

    /** Set the UI for successful token acquisition data */
    private fun updateSuccessUI() {
//        callGraphButton.visibility = View.INVISIBLE
//        signOutButton.visibility = View.VISIBLE
//        findViewById(R.id.welcome).setVisibility(View.VISIBLE)
//        (findViewById(R.id.welcome) as TextView).text = "Welcome, " + authResult.getUser().name
//        findViewById(R.id.graphData).setVisibility(View.VISIBLE)
    }

    /**
     * Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    fun onCallGraphClicked() {
        activity?.let {act ->
            sampleApp?.acquireToken(act, SCOPES, getAuthInteractiveCallback())
        }
    }

    /** Handles the redirect from the System Browser */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        sampleApp?.handleInteractiveRequestRedirect(requestCode, resultCode, data)
    }

    /** Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private fun callGraphAPI() {
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

                updateGraphUI(response)
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

    /** Sets the Graph response */
    private fun updateGraphUI(graphResponse: JSONObject) {
//        Log.d(TAG, "updateGraphUI: " + graphResponse.toString())

        // Save data to realm
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        try {
            var realmAzureAD: RealmAzureAD? = realm.where(RealmAzureAD::class.java).findFirst()
            realmAzureAD?.let { obj ->
                obj.odatacontext = graphResponse.optString("@odata.context")
                obj.id = graphResponse.optString("id")
                obj.businessPhones = graphResponse.optString("businessPhones")
                obj.displayName = graphResponse.optString("displayName")
                obj.givenName = graphResponse.optString("givenName")
                obj.jobTitle = graphResponse.optString("jobTitle")
                obj.mail = graphResponse.optString("mail")
                obj.mobilePhone = graphResponse.optString("mobilePhone")
                obj.officeLocation = graphResponse.optString("officeLocation")
                obj.preferredLanguage = graphResponse.optString("preferredLanguage")
                obj.surname = graphResponse.optString("surname")
                obj.userPrincipalName = graphResponse.optString("userPrincipalName")
            }
        } finally {
            realm.commitTransaction()
            realm.close()
        }

        callback?.callbackCall(true, true)

//        val graphText = findViewById(R.id.graphData) as TextView
//        graphText.text = graphResponse.toString()
    }

    /**
     * Clears a user's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     */
    fun onSignOutClicked() {

        /* Attempt to get a user and remove their cookies from cache */
        try {
            // Remove info from Realm
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            try {
                // Remove old object from Realm
                realm.delete(RealmAzureAD::class.java)
            } finally {
                realm.commitTransaction()
                realm.close()
            }

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
                updateSignedOutUI()
            }

            // BB 2018-10-23. Old 'getBaseContext()'.
//            Toast.makeText(activity, "Signed Out!", Toast.LENGTH_SHORT).show()

        } catch (e: MsalClientException) {
//            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString())

            callback?.callbackCall(false, true)

        } catch (e: IndexOutOfBoundsException) {
//            Log.d(TAG, "User at this position does not exist: " + e.toString())

            callback?.callbackCall(false, true)
        }

    }

    /** Set the UI for signed-out user */
    private fun updateSignedOutUI() {
        callback?.callbackCall(true, false)

//        callGraphButton.visibility = View.VISIBLE
//        signOutButton.visibility = View.INVISIBLE
//        findViewById(R.id.welcome).setVisibility(View.INVISIBLE)
//        findViewById(R.id.graphData).setVisibility(View.INVISIBLE)
//        (findViewById(R.id.graphData) as TextView).text = "No Data"
    }
}