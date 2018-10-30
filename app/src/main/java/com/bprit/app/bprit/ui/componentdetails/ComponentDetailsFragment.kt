package com.bprit.app.bprit.ui.componentdetails

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.bprit.app.bprit.R
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.interfaces.AlertDialogButtonOnClickListener
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.models.*
import io.realm.Realm

/**
 * Component details fragment
 */
class ComponentDetailsFragment : Fragment() {

    var serialHeadingTextView: TextView? = null
    var deleteButton: Button? = null
    var serialTextView: TextView? = null
    var typeTextView: TextView? = null
    var createdTextView: TextView? = null
    var modifiedTextView: TextView? = null

    var actionSyncMenuItem: MenuItem? = null

    private var componentId: Int? = null
    private var componentTypeId: Int? = null

    companion object {
        fun newInstance() = ComponentDetailsFragment()
    }

    private lateinit var viewModel: ComponentDetailsViewModel

    /**
     * Mark component as deleted, ready to synchronize
     */
    private val deleteButtonOnClickListener = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            activity?.let { act ->
                val global = Global()
                global.getConfirmAlertDialog(
                    act,
                    getString(R.string.componentDetails_componentWillBeRemoved),
                    object : AlertDialogButtonOnClickListener {
                        override fun onClick() {
                            val realmOperations = RealmOperations()
                            componentTypeId?.let { typeId ->
                                componentId?.let { id ->
                                    realmOperations.syncDeleteComponent(typeId, id)
                                }
                            }

                            showIfDataShouldSynchronize()
                        }
                    },
                    null
                ).show()
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

                                    checkIfActivityShouldFinish()
                                } else {
                                    global.getErrorAlertDialog(act, error, null).show()
                                }

                                viewModel.loadingAlertDialog?.setLoading(act, false, "")
                            }
                        }
                    }
                })
            } else {
                checkIfActivityShouldFinish()
            }
        }
    }

    /**
     * Check if component exist, if not close activity
     */
    fun checkIfActivityShouldFinish() {
        activity?.let { act ->
            componentTypeId?.let {typeId ->
                componentId?.let {id ->
                    val realmOperations = RealmOperations()
                    if (realmOperations.isComponentDeleted(typeId, id)) {
                        act.finish()
                        act.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    }
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

        viewModel.loadingAlertDialog?.isLoading?.let { isLoading ->
            if (!isLoading) {
                showIfDataShouldSynchronize()
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

        // Receives data from intent
        val extras = activity?.intent?.extras
        if (extras != null) {
            if (extras.containsKey("componentId")) componentId = extras.getInt("componentId")
            if (extras.containsKey("componentTypeId")) componentTypeId = extras.getInt("componentTypeId")
        } else {
            activity?.finish()
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
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
        return inflater.inflate(R.layout.component_details_fragment, container, false)
    }

    /**
     * Method called when activity is created.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ComponentDetailsViewModel::class.java)
        // Use the ViewModel

        // Set views
        serialHeadingTextView = activity?.findViewById(R.id.serialHeadingTextView)
        deleteButton = activity?.findViewById(R.id.deleteButton)
        serialTextView = activity?.findViewById(R.id.serialTextView)
        typeTextView = activity?.findViewById(R.id.typeTextView)
        createdTextView = activity?.findViewById(R.id.createdTextView)
        modifiedTextView = activity?.findViewById(R.id.modifiedTextView)

        // Init loading dialog
        if (viewModel.loadingAlertDialog == null) {
            activity?.let { act ->
                viewModel.loadingAlertDialog = LoadingAlertDialog(act)
            }
        }

        // Set on click listener on buttons
        deleteButton?.setOnClickListener(deleteButtonOnClickListener)

        // Set details
        val realm = Realm.getDefaultInstance()
        val realmComponent = realm?.where(RealmComponent::class.java)
            ?.equalTo("id", componentId.toString())
            ?.equalTo("typeId", componentTypeId)
            ?.equalTo("isDeleted", false)?.findFirst()

        val realmComponentType = realm?.where(RealmComponentType::class.java)
            ?.equalTo("id", componentTypeId)
            ?.equalTo("isDeleted", false)?.findFirst()

        realmComponent?.let { component ->
            realmComponentType?.let { componentType ->
                val dateTimeFunctions = DateTimeFunctions()
                serialHeadingTextView?.text = component.id.toString()
                serialTextView?.text = component.id.toString()
                typeTextView?.text = componentType.name
                createdTextView?.text = dateTimeFunctions.beautifyDate(component.created)
                modifiedTextView?.text = dateTimeFunctions.beautifyDate(component.modified)
            }
        }
        realm.close()
    }
}
