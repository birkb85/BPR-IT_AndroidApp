package com.bprit.app.bprit.ui.componentdetails

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bprit.app.bprit.R

class ComponentDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ComponentDetailsFragment()
    }

    private lateinit var viewModel: ComponentDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.component_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ComponentDetailsViewModel::class.java)
        // Use the ViewModel
    }

}
