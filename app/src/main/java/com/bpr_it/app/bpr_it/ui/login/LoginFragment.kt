package com.bpr_it.app.bpr_it.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bpr_it.app.bpr_it.R
import android.content.pm.PackageManager
import android.util.Log
import android.widget.*
import com.bpr_it.app.bpr_it.MenuActivity
import android.content.Intent


class LoginFragment : Fragment() {

    var linearLayout: LinearLayout? = null
    var usernameEditText: EditText? = null
    var passwordEditText: EditText? = null
    var rememberCheckBox: CheckBox? = null
    var loginButton: Button? = null
    var versionTextView: TextView? = null

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

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
        linearLayout = activity?.findViewById(R.id.linearLayout)
        usernameEditText = activity?.findViewById(R.id.usernameEditText)
        passwordEditText = activity?.findViewById(R.id.passwordEditText)
        rememberCheckBox = activity?.findViewById(R.id.rememberCheckBox)
        loginButton = activity?.findViewById(R.id.loginButton)
        versionTextView = activity?.findViewById(R.id.versionTextView)

        loginButton?.setOnClickListener {
            // TODO BB 2018-10-17. Temp go to menu.
            activity?.let {
                fragmentActivity ->
                fragmentActivity.finish()
                val intent = Intent(context, MenuActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
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

}
