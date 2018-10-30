package com.bprit.app.bprit.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bprit.app.bprit.R
import android.widget.*
import com.bprit.app.bprit.MenuActivity
import android.content.Intent
import com.bprit.app.bprit.data.AzureADGraphResponse
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.interfaces.CallbackAzureAD
import com.bprit.app.bprit.models.ApplicationInformation
import com.bprit.app.bprit.models.AzureAD
import com.bprit.app.bprit.models.Global
import com.bprit.app.bprit.models.LoadingAlertDialog
import io.realm.Realm

/**
 * Login fragment.
 */
class LoginFragment : Fragment() {

    private var signInOutButton: Button? = null
    private var menuButton: Button? = null
    private var versionTextView: TextView? = null

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    /**
     * Callback handling Azure AD result
     */
    private val azureADCallback: CallbackAzureAD = object : CallbackAzureAD {
        override fun callbackCall(success: Boolean, isSignedIn: Boolean) {
            if (success) {
                if (isSignedIn) {
                    signInOutButton?.text = getString(R.string.login_signOut)
                    menuButton?.visibility = View.VISIBLE

                    // Go to menu.
                    activity?.let { fragmentActivity ->
                        val intent = Intent(context, MenuActivity::class.java)
                        startActivity(intent)
                        fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                } else {
                    signInOutButton?.text = getString(R.string.login_signIn)
                    menuButton?.visibility = View.GONE
                }
            } else {
                if (isSignedIn) {
                    signInOutButton?.text = getString(R.string.login_signOut)
                    menuButton?.visibility = View.VISIBLE
                } else {
                    signInOutButton?.text = getString(R.string.login_signIn)
                    menuButton?.visibility = View.GONE
                }
            }

            activity?.let { act ->
                viewModel.loadingAlertDialog?.setLoading(act, false, "")
            }
        }
    }

    /**
     * Method called when fragment resumes.
     */
    override fun onResume() {
        super.onResume()

        // Restore loading
        viewModel.loadingAlertDialog?.onResume()
    }

    /**
     * Method called when fragment pauses.
     */
    override fun onPause() {
        super.onPause()

        // Close Loading Alert Dialog
        viewModel.loadingAlertDialog?.onPause()
    }

    /**
     * Method called when view is created.
     * @param inflater the layout inflator used to inflate the view into the fragment.
     * @param container view group container.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     * @return the view containing the inflated layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    /**
     * Method called when activity is created.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // Use the ViewModel

        // Set views
        signInOutButton = activity?.findViewById(R.id.signInOutButton)
        menuButton = activity?.findViewById(R.id.menuButton)
        versionTextView = activity?.findViewById(R.id.versionTextView)

        // Init loading dialog
        if (viewModel.loadingAlertDialog == null) {
            activity?.let { act ->
                viewModel.loadingAlertDialog = LoadingAlertDialog(act)
            }
        }

        // Set signInOutButton text
        if (Global.isSignedIn) {
            signInOutButton?.text = getString(R.string.login_signOut)
            menuButton?.visibility = View.VISIBLE
        } else {
            signInOutButton?.text = getString(R.string.login_signIn)
            menuButton?.visibility = View.GONE
        }

        menuButton?.setOnClickListener {
            // Go to menu.
            activity?.let { fragmentActivity ->
                val intent = Intent(context, MenuActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        // Set signInOutButton OnClickListener
        signInOutButton?.setOnClickListener {
            if (!Global.isSignedIn) {
                activity?.let { act ->
                    val global = Global()
                    if (global.isConnectedToInternet(act)) {
                        viewModel.loadingAlertDialog?.setLoading(act, true, getString(R.string.dialog_loading_login))
                        Global.azureAD?.signIn(act, azureADCallback)
                    } else {
                        global.getMessageAlertDialog(act, getString(R.string.login_notConnectedToInternet), null).show()
                    }
                }
            } else {
                activity?.let { act ->
                    viewModel.loadingAlertDialog?.setLoading(act, true, getString(R.string.dialog_loading_logout))
                    Global.azureAD?.signOut(act, azureADCallback)
                }
            }
        }

        // Show version
        context?.let { context ->
            val applicationInformation = ApplicationInformation()
            val version = context.getString(R.string.login_version) + " " + applicationInformation.getVersion(context)
            versionTextView?.text = version
        }
    }
}
