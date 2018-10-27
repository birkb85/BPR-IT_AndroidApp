package com.bprit.app.bprit.ui.menu

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.*
import com.bprit.app.bprit.ComponentTypeListActivity
import com.bprit.app.bprit.R
import com.bprit.app.bprit.TaskListActivity
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.models.*


class MenuFragment : Fragment() {

    var nameTextView: TextView? = null
    var dateTextView: TextView? = null
    var tasksLinearLayout: LinearLayout? = null
    var tasksDividerView: View? = null
    var componentsLinearLayout: LinearLayout? = null

    var actionSyncMenuItem: MenuItem? = null

    companion object {
        fun newInstance() = MenuFragment()
    }

    private lateinit var viewModel: MenuViewModel

    fun showIfDataShouldSynchronize() {
        val synchronizeData = SynchronizeData()
        actionSyncMenuItem?.isVisible = synchronizeData.shouldSynchronizeData()
    }

    override fun onResume() {
        super.onResume()

        showIfDataShouldSynchronize()

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
        setHasOptionsMenu(true) // TODO implement in other fragments!!!!!
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
                    override fun callbackCall(success: Boolean) {
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
        return inflater.inflate(R.layout.menu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        // Use the ViewModel

        // Set views
        nameTextView = activity?.findViewById(R.id.nameTextView)
        dateTextView = activity?.findViewById(R.id.dateTextView)
        tasksLinearLayout = activity?.findViewById(R.id.tasksLinearLayout)
        tasksDividerView = activity?.findViewById(R.id.tasksDividerView)
        componentsLinearLayout = activity?.findViewById(R.id.componentsLinearLayout)

        // Init loading dialog
        if (viewModel.loadingAlertDialog == null) {
            activity?.let { act ->
                viewModel.loadingAlertDialog = LoadingAlertDialog(act)
            }
        }

        // Set name
        Global.azureAD?.getDisplayName()?.let {name ->
            nameTextView?.text = name
        }

        // Set date
        val dateTimeFunctions = DateTimeFunctions()
        dateTextView?.text = dateTimeFunctions.beautifyDate(dateTimeFunctions.getCurrentDate())

        tasksLinearLayout?.setOnClickListener {
//            val webservice = Webservice()
//            webservice.testStatusTypes(object : CallbackWebserviceResult {
//                override fun callbackCall(result: WebserviceResult) {
//                    if (result.success) {
//                        Log.d("DEBUG", "success: " + result.success.toString())
//                        Log.d("DEBUG", "error: " + result.error)
//                    } else {
//                        activity?.let { act ->
//                            act.runOnUiThread {
//                                val global = Global()
//                                global.getErrorAlertDialog(
//                                    act,
//                                    global.errorMessage(
//                                        act,
//                                        result.error
//                                    ),
//                                    null
//                                ).show()
//                            }
//                        }
//                    }
//                }
//            })

            // TODO BB 2018-10-17. Temp go to task list.
            activity?.let { fragmentActivity ->
                val intent = Intent(context, TaskListActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        componentsLinearLayout?.setOnClickListener {
            // TODO BB 2018-10-17. Temp go to component type list.
            activity?.let { fragmentActivity ->
                val intent = Intent(context, ComponentTypeListActivity::class.java)
                startActivity(intent)
                fragmentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

}
