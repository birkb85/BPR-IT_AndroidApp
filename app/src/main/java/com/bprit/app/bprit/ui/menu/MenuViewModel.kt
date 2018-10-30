package com.bprit.app.bprit.ui.menu

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Menu view model.
 */
class MenuViewModel : ViewModel() {

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
