package com.bprit.app.bprit.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bprit.app.bprit.R
import android.content.pm.PackageManager
import android.widget.*
import com.bprit.app.bprit.MenuActivity
import android.content.Intent
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackWebservice
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import com.bprit.app.bprit.model.AzureAD
import com.bprit.app.bprit.model.Webservice
import com.microsoft.identity.client.*
import kotlinx.android.synthetic.main.menu_fragment.view.*
import org.json.JSONObject
import java.util.HashMap


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    var nameTextView: TextView? = null
    var dataTextView: TextView? = null
    var signInOutButton: Button? = null
//    var linearLayout: LinearLayout? = null
//    var usernameEditText: EditText? = null
//    var passwordEditText: EditText? = null
//    var rememberCheckBox: CheckBox? = null
//    var loginButton: Button? = null
    var versionTextView: TextView? = null

//    private var azureAD: AzureAD? = null

    private val CLIENT_ID = "ea3501cb-f257-4d92-96e5-1f534d4343d1" //"[Enter the application Id here]";
    private val SCOPES = arrayOf("https://graph.microsoft.com/User.Read")
    private val MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me"

    /* UI & Debugging Variables */
    private val TAG = "DEBUG" //MainActivity.class.getSimpleName();

    /* Azure AD Variables */
    private var sampleApp: PublicClientApplication? = null
    private var authResult: AuthenticationResult? = null

//    fun showAlert() {
//        // Initialize a new instance of
//        context?.let {
//            val builder = AlertDialog.Builder(it)
//
//            // Set the alert dialog title
//            builder.setTitle("App background color")
//
//            // Display a message on alert dialog
//            builder.setMessage("Are you want to set the app background color to RED?")
//
//            // Set a positive button and its click listener on alert dialog
//            builder.setPositiveButton("YES"){dialog, which ->
//                // Do something when user press the positive button
//                Toast.makeText(it,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()
//
//                // Change the app background color
//                //root_layout.setBackgroundColor(Color.RED)
//            }
//
//
//            // Display a negative button on alert dialog
//            builder.setNegativeButton("No"){dialog,which ->
//                Toast.makeText(it,"You are not agree.",Toast.LENGTH_SHORT).show()
//            }
//
//
//            // Display a neutral button on alert dialog
//            builder.setNeutralButton("Cancel"){_,_ ->
//                Toast.makeText(it,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
//            }
//
//            // Finally, make the alert dialog using builder
//            val dialog: AlertDialog = builder.create()
//
//            // Display the alert dialog on app interface
//            dialog.show()
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // Use the ViewModel

        // Set views
        nameTextView = activity?.findViewById(R.id.nameTextView)
        dataTextView = activity?.findViewById(R.id.dataTextView)
        signInOutButton = activity?.findViewById(R.id.signInOutButton)
//        linearLayout = activity?.findViewById(R.id.linearLayout)
//        usernameEditText = activity?.findViewById(R.id.usernameEditText)
//        passwordEditText = activity?.findViewById(R.id.passwordEditText)
//        rememberCheckBox = activity?.findViewById(R.id.rememberCheckBox)
//        loginButton = activity?.findViewById(R.id.loginButton)
        versionTextView = activity?.findViewById(R.id.versionTextView)

//        // Init AzureAD
//        activity?.let {act ->
//            azureAD = AzureAD(act)
//        }

        /* Configure your sample app and save state for this activity */
        activity?.let {act ->
            sampleApp = null
            if (sampleApp == null) {
                sampleApp = PublicClientApplication(
                    act.applicationContext,
                    CLIENT_ID
                )
            }
        }

        signInOutButton?.setOnClickListener {
            // TODO BB 2018-10-17. Testing AzureAD login
//            azureAD?.onCallGraphClicked()

            onCallGraphClicked()

//            // TODO BB 2018-10-17. Temp go to menu.
//            activity?.let { fragmentActivity ->
//
//                Log.d("DEBUG", "Test")
//
//                val webservice = Webservice()
//                webservice.testOrder(object : CallbackWebserviceResult {
//                    override fun callbackCall(result: WebserviceResult) {
//                        Log.d("DEBUG", "success: " + result.success.toString())
//                        Log.d("DEBUG", "error: " + result.error)
//                    }
//                })
//
//
////                fragmentActivity.finish()
////                val intent = Intent(context, MenuActivity::class.java)
////                startActivity(intent)
////                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            }
        }

        // TODO BB 2018-10-17. Get version text from controller.
        // Show version
        try {
            context?.let {
                context ->
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val version = getString(R.string.login_version) + " " + pInfo.versionName
                versionTextView?.setText(version)
            }
        } catch (e: PackageManager.NameNotFoundException) {
//            Global.showExceptionAlertDialog(e, activity, alertDialogs)
//            Crashlytics.logException(e)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        azureAD?.onActivityResult(requestCode, resultCode, data)
//
////        super.onActivityResult(requestCode, resultCode, data)
//    }

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
        val text = "Welcome, " + authResult?.user?.name
        nameTextView?.text = text
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
        activity?.let {act ->
            sampleApp?.acquireToken(act, SCOPES, getAuthInteractiveCallback())
        }
    }

    /* Handles the redirect from the System Browser */
    fun onActivityResultTest(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
        dataTextView?.text = graphResponse.toString()
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
        nameTextView?.text = ""
        dataTextView?.text = ""
//        callGraphButton.visibility = View.VISIBLE // TODO BB 2018-10-23.
//        signOutButton.visibility = View.INVISIBLE // TODO BB 2018-10-23.
//        findViewById(R.id.welcome).setVisibility(View.INVISIBLE) // TODO BB 2018-10-23.
//        findViewById(R.id.graphData).setVisibility(View.INVISIBLE) // TODO BB 2018-10-23.
//        (findViewById(R.id.graphData) as TextView).text = "No Data" // TODO BB 2018-10-23.
    }
}
