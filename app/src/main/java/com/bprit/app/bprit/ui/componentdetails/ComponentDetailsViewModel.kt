package com.bprit.app.bprit.ui.componentdetails

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Component details view model.
 */
class ComponentDetailsViewModel : ViewModel() {

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
