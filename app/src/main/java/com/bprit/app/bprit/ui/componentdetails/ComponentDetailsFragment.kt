package com.bprit.app.bprit.ui.componentdetails

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bprit.app.bprit.R
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.models.DateTimeFunctions
import com.bprit.app.bprit.models.Global
import com.bprit.app.bprit.models.LoadingAlertDialog
import com.bprit.app.bprit.models.SynchronizeData
import io.realm.Realm

class ComponentDetailsFragment : Fragment() {

    var serialHeadingTextView: TextView? = null
    var deleteButton: Button? = null
    var serialTextView: TextView? = null
    var typeTextView: TextView? = null
    var createdTextView: TextView? = null
    var modifiedTextView: TextView? = null

    private var componentId: Int? = null

    companion object {
        fun newInstance() = ComponentDetailsFragment()
    }

    private lateinit var viewModel: ComponentDetailsViewModel

    private val deleteButtonOnClickListener = object : View.OnClickListener {
        override fun onClick(view: View?) {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            try {
                val realmComponent = realm?.where(RealmComponent::class.java)?.equalTo("id", componentId.toString())?.findFirst()
                realmComponent?.let { component ->
                    component.isDeleted = true
                    component.shouldSynchronize = true
                }
            } finally {
                realm.commitTransaction()
                realm.close()
            }

            val synchronizeData = SynchronizeData()
            if (synchronizeData.shouldSynchronizeData()) {
                activity?.finish()
                activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity == null) return

        // Receives data from intent
        val extras = activity?.intent?.extras
        if (extras != null) {
            if (extras.containsKey("componentId")) componentId = extras.getInt("componentId")
        } else {
            activity?.finish()
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

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
        val realmComponent = realm.where(RealmComponent::class.java).equalTo("id", componentId.toString()).findFirst()
        realmComponent?.let {component ->
            val realmComponentType = realm.where(RealmComponentType::class.java).equalTo("id", component.typeId).findFirst()
            realmComponentType?.let {componentType ->
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
