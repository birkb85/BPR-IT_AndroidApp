package com.bprit.app.bprit.ui.login

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.model.AzureAD

class LoginViewModel : ViewModel() {
    // Implement the ViewModel

    var isSignedIn: Boolean = false

    var azureAD: AzureAD? = null
}
