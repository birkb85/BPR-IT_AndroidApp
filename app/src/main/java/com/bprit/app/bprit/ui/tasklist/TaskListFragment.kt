package com.bprit.app.bprit.ui.tasklist

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
import com.bprit.app.bprit.R
import android.support.v7.widget.RecyclerView
import com.bprit.app.bprit.TaskDetailsActivity
import com.bprit.app.bprit.models.LoadingAlertDialog

class TaskListFragment : Fragment() {

    var filterEditText: EditText? = null
    var detailsButton: Button? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance() = TaskListFragment()
    }

    private lateinit var viewModel: TaskListViewModel

    override fun onResume() {
        super.onResume()

        // Restore loading
        viewModel.loadingAlertDialog?.onResume()
    }

    override fun onPause() {
        super.onPause()

        // Close Loading Alert Dialog
        viewModel.loadingAlertDialog?.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.task_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TaskListViewModel::class.java)
        // Use the ViewModel

        // Set views
        filterEditText = activity?.findViewById(R.id.filterEditText)
        detailsButton = activity?.findViewById(R.id.detailsButton)
        swipeRefreshLayout = activity?.findViewById(R.id.swipeRefreshLayout)
        recyclerView = activity?.findViewById(R.id.recyclerView)

        // Init loading dialog
        if (viewModel.loadingAlertDialog == null) {
            activity?.let { act ->
                viewModel.loadingAlertDialog = LoadingAlertDialog(act)
            }
        }

        detailsButton?.setOnClickListener {
            // TODO BB 2018-10-17. Temp go to task details.
            activity?.let { fragmentActivity ->
                val intent = Intent(context, TaskDetailsActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

}
