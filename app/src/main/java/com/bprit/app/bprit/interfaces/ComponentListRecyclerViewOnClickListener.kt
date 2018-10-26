package com.bprit.app.bprit.interfaces

import android.view.View

/**
 * Component list recycler view on click listener
 */
interface ComponentListRecyclerViewOnClickListener {
    /**
     * Component list recycler view on click
     * @param view the view clicked
     * @param id the id of the component
     */
    fun onClick(view: View, id: Int)
}