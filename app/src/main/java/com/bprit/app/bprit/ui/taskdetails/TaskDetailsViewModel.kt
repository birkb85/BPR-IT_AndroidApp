package com.bprit.app.bprit.ui.taskdetails

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Task details view model.
 */
class TaskDetailsViewModel : ViewModel() {

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
