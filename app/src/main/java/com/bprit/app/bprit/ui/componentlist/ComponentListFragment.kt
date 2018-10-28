package com.bprit.app.bprit.ui.componentlist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import com.bprit.app.bprit.R
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import com.bprit.app.bprit.ComponentDetailsActivity
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import com.bprit.app.bprit.interfaces.ComponentListRecyclerViewOnClickListener
import com.bprit.app.bprit.interfaces.ComponentTypeListRecyclerViewOnClickListener
import com.bprit.app.bprit.models.*

class ComponentListFragment : Fragment() {

    var filterEditText: EditText? = null
    var notConnectedToInternetTextView: TextView? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null

    var actionSyncMenuItem: MenuItem? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private var componentListRecyclerAdapter: ComponentListRecyclerAdapter? = null

    private var componentTypeId: Int? = null

    companion object {
        fun newInstance() = ComponentListFragment()
    }

    private lateinit var viewModel: ComponentListViewModel

    /**
     * Filter list when typing text in filter
     */
    private var filterEditTextTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun afterTextChanged(editable: Editable) {
            filterList()
        }
    }

    /**
     * Filter the list
     */
    private fun filterList() {
        filterEditText?.let { editText ->
            componentListRecyclerAdapter?.filterList(editText.text.toString())
        }
    }

    /**
     * Handles click on cell in list
     */
    private var componentListRecyclerViewOnClickListener: ComponentListRecyclerViewOnClickListener =
        object : ComponentListRecyclerViewOnClickListener {
            override fun onClick(view: View, id: Int?) {
//                Toast.makeText(context, "id: $id", Toast.LENGTH_SHORT).show()

                activity?.let { fragmentActivity ->
                    val intent = Intent(context, ComponentDetailsActivity::class.java)
                    intent.putExtra("componentId", id)
                    intent.putExtra("componentTypeId", componentTypeId)
                    startActivity(intent)
                    fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
        }

    /**
     * Show if data should synchronize
     */
    fun showIfDataShouldSynchronize() {
        val synchronizeData = SynchronizeData()
        actionSyncMenuItem?.isVisible = synchronizeData.shouldSynchronizeData()
    }

    override fun onResume() {
        super.onResume()

        // Try to update list if not updated yet
        if (!viewModel.listUpdated) {
            activity?.let {act ->
                val global = Global()
                if (global.isConnectedToInternet(act)) {
                    viewModel.loadingAlertDialog?.setLoading(
                        act,
                        true,
                        getString(R.string.dialog_loading_getComponents)
                    )

                    componentTypeId?.let { typeId ->
                        val webservice = Webservice()
                        webservice.getComponentsForType(typeId, object : CallbackWebserviceResult {
                            override fun callbackCall(result: WebserviceResult) {
                                activity?.let { act ->
                                    act.runOnUiThread {
                                        if (result.success) {
                                            notConnectedToInternetTextView?.visibility = View.GONE
                                            viewModel.listUpdated = true
                                            filterList()
                                        } else {
                                            global.getErrorAlertDialog(act, result.error, null).show()
                                        }

                                        viewModel.loadingAlertDialog?.setLoading(act, false, "")
                                    }
                                }
                            }
                        })
                    }
                } else {
                    notConnectedToInternetTextView?.visibility = View.VISIBLE
                }
            }
        }

        showIfDataShouldSynchronize()

        filterList()

        // Restore loading
        viewModel.loadingAlertDialog?.onResume()
    }

    override fun onPause() {
        super.onPause()

        // Close Loading Alert Dialog
        viewModel.loadingAlertDialog?.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Receives data from intent
        val extras = activity?.intent?.extras
        if (extras != null) {
            if (extras.containsKey("componentTypeId")) componentTypeId = extras.getInt("componentTypeId")
        } else {
            activity?.finish()
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        actionSyncMenuItem = menu?.findItem(R.id.action_sync)
        showIfDataShouldSynchronize()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_sync -> {
                val synchronizeData = SynchronizeData()
                synchronizeData.synchronizeData(object : CallbackSynchronizeData {
                    override fun callbackCall(success: Boolean, error: String) {
                        item.isVisible = !success
                    }
                })
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

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
        notConnectedToInternetTextView = activity?.findViewById(R.id.notConnectedToInternetTextView)
        swipeRefreshLayout = activity?.findViewById(R.id.swipeRefreshLayout)
        recyclerView = activity?.findViewById(R.id.recyclerView)

        // Init loading dialog
        if (viewModel.loadingAlertDialog == null) {
            activity?.let { act ->
                viewModel.loadingAlertDialog = LoadingAlertDialog(act)
            }
        }

        filterEditText?.addTextChangedListener(filterEditTextTextWatcher)

        // Hide not connected to internet message on create
        notConnectedToInternetTextView?.visibility = View.GONE

        // Recycler view
        componentTypeId?.let { id ->
            recyclerView?.setHasFixedSize(false)
            linearLayoutManager = LinearLayoutManager(context)
            recyclerView?.layoutManager = linearLayoutManager
            componentListRecyclerAdapter = ComponentListRecyclerAdapter(componentListRecyclerViewOnClickListener, id)
            recyclerView?.adapter = componentListRecyclerAdapter
        }

        // TODO Implement swipe to refresh
    }

}
