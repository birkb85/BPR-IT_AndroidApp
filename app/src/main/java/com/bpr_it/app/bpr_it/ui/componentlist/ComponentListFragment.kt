package com.bpr_it.app.bpr_it.ui.componentlist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.bpr_it.app.bpr_it.R
import android.support.v7.widget.RecyclerView
import com.bpr_it.app.bpr_it.ComponentDetailsActivity
import com.bpr_it.app.bpr_it.TaskDetailsActivity

class ComponentListFragment : Fragment() {

    var filterEditText: EditText? = null
    var detailsButton: Button? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance() = ComponentListFragment()
    }

    private lateinit var viewModel: ComponentListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.component_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ComponentListViewModel::class.java)
        // Use the ViewModel

        // Set views
        filterEditText = activity?.findViewById(R.id.filterEditText)
        detailsButton = activity?.findViewById(R.id.detailsButton)
        swipeRefreshLayout = activity?.findViewById(R.id.swipeRefreshLayout)
        recyclerView = activity?.findViewById(R.id.recyclerView)

        detailsButton?.setOnClickListener {
            // TODO BB 2018-10-17. Temp go to task details.
            activity?.let { fragmentActivity ->
                val intent = Intent(context, ComponentDetailsActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

}
