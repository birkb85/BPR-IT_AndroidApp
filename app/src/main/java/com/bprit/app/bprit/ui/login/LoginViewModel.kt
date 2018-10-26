package com.bprit.app.bprit.ui.login

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.model.AzureAD
import com.bprit.app.bprit.model.LoadingAlertDialog

class LoginViewModel : ViewModel() {
    // Implement the ViewModel

    var loadingAlertDialog: LoadingAlertDialog? = null

    var isSignedIn: Boolean = false

    var azureAD: AzureAD? = null
}
