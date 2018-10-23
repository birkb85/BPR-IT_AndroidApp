package com.bprit.app.bprit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bprit.app.bprit.ui.login.LoginFragment
import android.R.attr.fragment



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragment = supportFragmentManager.findFragmentById(R.id.container) as LoginFragment
        fragment.onActivityResultTest(requestCode, resultCode, data)
//        fragment.azureAD? // TODO
    }
}
