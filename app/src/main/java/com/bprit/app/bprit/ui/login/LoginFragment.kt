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


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    var signInOutButton: Button? = null
    var menuButton: Button? = null
    var versionTextView: TextView? = null

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

    //                // Example on calling Realm
//                val realm = Realm.getDefaultInstance()
//                realm.beginTransaction()
//                try {
//                    // Remove old object from Realm
//                    realm.delete(AzureADGraphResponse::class.java)
//
//                    // Create new object in Realm
//                    val realmAzureAD = AzureADGraphResponse()
//                    realmAzureAD.idToken = authenticationResult.idToken
//                    realm.copyToRealm(realmAzureAD)
//                } finally {
//                    realm.commitTransaction()
//                    realm.close()
//                }

//    fun setAzureADOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Global.azureAD?.onActivityResult(requestCode, resultCode, data)
//    }

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

    override fun onResume() {
        super.onResume()

        // Restore loading
        viewModel.loadingAlertDialog?.onResume()
    }

    override fun onPause() {
        super.onPause()

        // Close Loading Alert Dialog
        viewModel.loadingAlertDialog?.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

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

//        // BB 2018-10-26. Remove at release. Test create componenttypes.
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        try {
//            val realmComponentTypeRealmResults = realm.where(RealmComponentType::class.java).findAll()
//            realmComponentTypeRealmResults?.let { results ->
//                if (results.size == 0) {
//                    for (i in 1..10) {
//                        // Create new objects in Realm
//                        val realmComponentType = RealmComponentType()
//                        realmComponentType.id = i
//                        realmComponentType.name = "Test $i"
//                        realmComponentType.inStorage = 10
////                        if (i == 9) {
////                            realmComponentType.isDeleted = true
////                            realmComponentType.shouldSynchronize = true
////                        }
//                        realm.copyToRealm(realmComponentType)
//                    }
//                }
//            }
//
//            val realmComponentRealmResults = realm.where(RealmComponent::class.java).findAll()
//            realmComponentRealmResults?.let { results ->
//                if (results.size == 0) {
//                    for (i in 1..10) {
//                        val realmComponentType = realm.where(RealmComponentType::class.java).equalTo("id", 1 as Int).findFirst()
//                        if (realmComponentType == null) {
//                            val realmComponentTypeNew = RealmComponentType()
//                            realmComponentTypeNew.id = 1
//                            realmComponentTypeNew.name = "Test 1"
//                            realmComponentTypeNew.inStorage = 10
//                            realm.copyToRealm(realmComponentTypeNew)
//                        }
//
//                        val realmComponent = RealmComponent()
//                        realmComponent.id = i.toString()
//                        realmComponent.typeId = 1
//                        if (i == 9) {
//                            realmComponent.isDeleted = true
//                            realmComponent.shouldSynchronize = true
//                        }
//                        realm.copyToRealm(realmComponent)
//                    }
//                }
//            }
//        } finally {
//            realm.commitTransaction()
//            realm.close()
//        }
    }
}
