package com.bprit.app.bprit.interfaces

import android.view.View

/**
 * Component type list recycler view on click listener.
 */
interface ComponentTypeListRecyclerViewOnClickListener {
    /**
     * Component type list recycler view on click.
     * @param view the view clicked.
     * @param id the id of the component type.
     */
    fun onClick(view: View, id: Int?)
}