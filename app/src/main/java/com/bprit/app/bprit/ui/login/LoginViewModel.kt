package com.bprit.app.bprit.ui.login

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.AzureAD
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Login view model.
 */
class LoginViewModel : ViewModel() {

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
