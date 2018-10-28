package com.bprit.app.bprit.ui.componentlist

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

class ComponentListViewModel : ViewModel() {
    // Implement the ViewModel

    var listUpdated = false

    var loadingAlertDialog: LoadingAlertDialog? = null
}
