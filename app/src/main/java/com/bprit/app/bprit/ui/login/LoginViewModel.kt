package com.bprit.app.bprit.ui.login

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.AzureAD
import com.bprit.app.bprit.models.LoadingAlertDialog

class LoginViewModel : ViewModel() {
    // Implement the ViewModel

    var loadingAlertDialog: LoadingAlertDialog? = null

    var isSignedIn: Boolean = false

    var azureAD: AzureAD? = null
}
