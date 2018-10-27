package com.bprit.app.bprit.ui.menu

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bprit.app.bprit.ComponentTypeListActivity
import com.bprit.app.bprit.R
import com.bprit.app.bprit.TaskListActivity
import com.bprit.app.bprit.data.WebserviceResult
import com.bprit.app.bprit.interfaces.CallbackWebserviceResult
import com.bprit.app.bprit.models.DateTimeFunctions
import com.bprit.app.bprit.models.Global
import com.bprit.app.bprit.models.LoadingAlertDialog
import com.bprit.app.bprit.models.Webservice

class MenuFragment : Fragment() {

    var nameTextView: TextView? = null
    var dateTextView: TextView? = null
    var tasksLinearLayout: LinearLayout? = null
    var tasksDividerView: View? = null
    var componentsLinearLayout: LinearLayout? = null

    companion object {
        fun newInstance() = MenuFragment()
    }

    private lateinit var viewModel: MenuViewModel

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
