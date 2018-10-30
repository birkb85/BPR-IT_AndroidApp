package com.bprit.app.bprit.ui.componenttypelist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import com.bprit.app.bprit.ComponentDetailsActivity
import com.bprit.app.bprit.ComponentListActivity
import com.bprit.app.bprit.R
import com.bprit.app.bprit.R.id.recyclerView
import io.realm.Sort
import android.support.v7.widget.LinearLayoutManager
import android.telephony.gsm.GsmCellLocation
import android.widget.Toast
import com.bprit.app.bprit.interfaces.ComponentTypeListRecyclerViewOnClickListener
import java.time.Duration
import com.bprit.app.bprit.R.id.filterEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import com.bprit.app.bprit.models.*

/**
 * Component type list fragment.
 */
class ComponentTypeListFragment : Fragment() {

    private var filterEditText: EditText? = null
    private var notConnectedToInternetTextView: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null

    private var actionSyncMenuItem: MenuItem? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private var componentTypeListRecyclerAdapter: ComponentTypeListRecyclerAdapter? = null

    companion object {
        fun newInstance() = ComponentTypeListFragment()
    }

    private lateinit var viewModel: ComponentTypeListViewModel

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
            componentTypeListRecyclerAdapter?.filterList(editText.text.toString())
        }
    }

    /**
     * Handles click on cell in list
     */
    private var componentTypeListRecyclerViewOnClickListener: ComponentTypeListRecyclerViewOnClickListener =
        object : ComponentTypeListRecyclerViewOnClickListener {
            override fun onClick(view: View, id: Int?) {
//                Toast.makeText(context, "id: $id", Toast.LENGTH_SHORT).show()

                activity?.let { fragmentActivity ->
                    val intent = Intent(context, ComponentListActivity::class.java)
                    intent.putExtra("componentTypeId", id)
                    startActivity(intent)
                    fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
        }

    /**
     * Show if data should synchronize
     */
    private fun showIfDataShouldSynchronize() {
        val synchronizeData = SynchronizeData()
        val shouldSynchronizeData = synchronizeData.shouldSynchronizeData()

        actionSyncMenuItem?.isVisible = shouldSynchronizeData

        activity?.let { act ->
            val global = Global()
            if (shouldSynchronizeData && global.isConnectedToInternet(act)) {
                viewModel.loadingAlertDialog?.setLoading(
                    act,
                    true,
                    getString(R.string.dialog_loading_synchronizeData)
                )

                synchronizeData.synchronizeData(object : CallbackSynchronizeData {
                    override fun callbackCall(success: Boolean, error: String) {
                        activity?.let { act ->
                            act.runOnUiThread {
                                if (success) {
                                    actionSyncMenuItem?.isVisible = false
                                } else {
                                    global.getErrorAlertDialog(act, error, null).show()
                                }

                                viewModel.loadingAlertDialog?.setLoading(act, false, "")
                            }
                        }
                    }
                })
            }
        }
    }

    private var swipeRefreshLayoutOnRefreshListener: SwipeRefreshLayout.OnRefreshListener =
        SwipeRefreshLayout.OnRefreshListener {
            viewModel.listUpdated = false
            updateList()
        }

    /**
     * Try to update list if not updated yet
     */
    private fun updateList() {
        if (!viewModel.listUpdated) {
            activity?.let { act ->
                val global = Global()
                if (global.isConnectedToInternet(act)) {
                    val synchronizeData = SynchronizeData()
                    if (!synchronizeData.shouldSynchronizeData()) {
                        viewModel.loadingAlertDialog?.setLoading(
                            act,
                            true,
                            getString(R.string.dialog_loading_getComponentTypes)
                        )

                        val webservice = Webservice()
                        webservice.getComponentTypes(object : CallbackWebserviceResult {
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
                                        swipeRefreshLayout?.isRefreshing = false
                                    }
                                }
                            }
                        })
                    } else {
                        notConnectedToInternetTextView?.text = getString(R.string.componentTypeList_synchronizeBeforeReloadData)
                        notConnectedToInternetTextView?.visibility = View.VISIBLE
                        swipeRefreshLayout?.isRefreshing = false
                    }
                } else {
                    notConnectedToInternetTextView?.text = getString(R.string.componentTypeList_notConnectedToInternet)
                    notConnectedToInternetTextView?.visibility = View.VISIBLE
                    swipeRefreshLayout?.isRefreshing = false
                }
            }
        }
    }

    /**
     * Method called when fragment resumes.
     */
    override fun onResume() {
        super.onResume()

        // Restore loading
        viewModel.loadingAlertDialog?.onResume()

        viewModel.loadingAlertDialog?.isLoading?.let {isLoading ->
            if (!isLoading) {
                updateList()

                showIfDataShouldSynchronize()

                filterList()
            }
        }
    }

    /**
     * Method called when fragment pauses.
     */
    override fun onPause() {
        super.onPause()

        // Close Loading Alert Dialog
        viewModel.loadingAlertDialog?.onPause()
    }

    /**
     * Method called when fragment is created.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * Method called when options menu is created.
     * @param menu the menu instance object created.
     * @param inflater the menu inflater.
     */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        actionSyncMenuItem = menu?.findItem(R.id.action_sync)
        showIfDataShouldSynchronize()
    }

    /**
     * Method called when option is selection in option menu.
     * @param item the item selected.
     * @return return true if 'item selected' event is handled here, else return the event.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_sync -> {
                activity?.let { act ->
                    val global = Global()
                    if (global.isConnectedToInternet(act)) {
                        val synchronizeData = SynchronizeData()
                        synchronizeData.synchronizeData(object : CallbackSynchronizeData {
                            override fun callbackCall(success: Boolean, error: String) {
                                activity?.let { act ->
                                    act.runOnUiThread {
                                        item.isVisible = !success
                                    }
                                }
                            }
                        })
                    } else {
                        global.getMessageAlertDialog(
                            act,
                            getString(R.string.componentDetails_notConnectedToInternet),
                            null
                        ).show()
                    }

                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Method called when view is created.
     * @param inflater the layout inflator used to inflate the view into the fragment.
     * @param container view group container.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     * @return the view containing the inflated layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.component_type_list_fragment, container, false)
    }

    /**
     * Method called when activity is created.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ComponentTypeListViewModel::class.java)
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
        recyclerView?.setHasFixedSize(false)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = linearLayoutManager
        componentTypeListRecyclerAdapter =
                ComponentTypeListRecyclerAdapter(componentTypeListRecyclerViewOnClickListener)
        recyclerView?.adapter = componentTypeListRecyclerAdapter

        // Swipe to refresh
        swipeRefreshLayout?.setOnRefreshListener(swipeRefreshLayoutOnRefreshListener);
    }

}
