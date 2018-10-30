package com.bprit.app.bprit.ui.tasklist

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

/**
 * Task list view model.
 */
class TaskListViewModel : ViewModel() {

    /**
     * Instance of loading dialog to be displayed in this activity.
     */
    var loadingAlertDialog: LoadingAlertDialog? = null
}
