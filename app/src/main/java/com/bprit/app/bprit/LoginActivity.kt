package com.bprit.app.bprit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bprit.app.bprit.ui.login.LoginFragment
import android.R.attr.fragment
import android.view.KeyEvent
import com.bprit.app.bprit.models.Global

/**
 * Login activity.
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Method called when activity is created.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }

    /**
     * Method called when activity receives a result from an intent.
     * @param requestCode the request code of the intent.
     * @param resultCode the result code of the result.
     * @param data the data received in the result of the intent.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Global.azureAD?.onActivityResult(requestCode, resultCode, data)

        // BB 2018-10-27. Example on how to get fragment.
//        val fragment = supportFragmentManager.findFragmentById(R.id.container) as LoginFragment
//        fragment.setAzureADOnActivityResult(requestCode, resultCode, data)
    }

    /**
     * Method called when key is pressed on device.
     * @param keyCode the key code of the key.
     * @param event the event of the key.
     * @return returns true if key is handled in method, else event is returned.
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // Back button is clicked
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
