package com.bprit.app.bprit.ui.componenttypelist

import android.arch.lifecycle.ViewModel
import com.bprit.app.bprit.models.LoadingAlertDialog

class ComponentTypeListViewModel : ViewModel() {
    // Implement the ViewModel

    var listUpdated = false

    var loadingAlertDialog: LoadingAlertDialog? = null
}
