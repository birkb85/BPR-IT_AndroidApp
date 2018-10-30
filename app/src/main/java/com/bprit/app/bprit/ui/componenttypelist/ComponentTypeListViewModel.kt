package com.bprit.app.bprit.ui.componenttypelist

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Component type list view model.
 */
class ComponentTypeListViewModel : ViewModel() {

    /**
     * Flag showing if list has been updated.
     */
    var listUpdated: Boolean = false

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
