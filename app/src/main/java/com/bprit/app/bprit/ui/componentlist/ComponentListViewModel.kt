package com.bprit.app.bprit.ui.componentlist

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Component list view model.
 */
class ComponentListViewModel : ViewModel() {

    /**
     * Flag showing if list has been updated.
     */
    var listUpdated: Boolean = false

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
