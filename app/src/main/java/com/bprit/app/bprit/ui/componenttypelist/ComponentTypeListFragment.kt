package com.bprit.app.bprit.ui.componenttypelist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.bprit.app.bprit.ComponentDetailsActivity
import com.bprit.app.bprit.ComponentListActivity
import com.bprit.app.bprit.R
import com.bprit.app.bprit.R.id.recyclerView
import io.realm.Sort
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.bprit.app.bprit.interfaces.ComponentTypeListRecyclerViewOnClickListener
import com.bprit.app.bprit.models.ComponentTypeListRecyclerAdapter
import java.time.Duration
import com.bprit.app.bprit.R.id.filterEditText
import android.text.Editable
import android.text.TextWatcher
import com.bprit.app.bprit.models.LoadingAlertDialog


class ComponentTypeListFragment : Fragment() {

    var filterEditText: EditText? = null
    var detailsButton: Button? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null

    var linearLayoutManager: LinearLayoutManager? = null
    var componentTypeListRecyclerAdapter: ComponentTypeListRecyclerAdapter? = null

    companion object {
        fun newInstance() = ComponentTypeListFragment()
    }

    private lateinit var viewModel: ComponentTypeListViewModel

    var filterEditTextTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun afterTextChanged(editable: Editable) {
            filterEditText?.let { editText ->
                componentTypeListRecyclerAdapter?.filterList(editText.text.toString())
            }
        }
    }

    var componentTypeListRecyclerViewOnClickListener: ComponentTypeListRecyclerViewOnClickListener =
        object : ComponentTypeListRecyclerViewOnClickListener {
            override fun onClick(view: View, id: Int?) {
                Toast.makeText(context, "id: $id", Toast.LENGTH_SHORT).show()

//                activity?.let { fragmentActivity ->
//                    val intent = Intent(context, ComponentListActivity::class.java)
//                    intent.putExtra("typeId", id)
//                    startActivity(intent)
//                    fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                }
            }
        }

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
        return inflater.inflate(R.layout.component_type_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ComponentTypeListViewModel::class.java)
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

        filterEditText?.addTextChangedListener(filterEditTextTextWatcher)

        detailsButton?.setOnClickListener {
            // TODO BB 2018-10-17. Temp go to component list.
            activity?.let { fragmentActivity ->
                val intent = Intent(context, ComponentListActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        // Recycler view
        recyclerView?.setHasFixedSize(false)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = linearLayoutManager
        componentTypeListRecyclerAdapter =
                ComponentTypeListRecyclerAdapter(componentTypeListRecyclerViewOnClickListener)
        recyclerView?.adapter = componentTypeListRecyclerAdapter
    }

}
