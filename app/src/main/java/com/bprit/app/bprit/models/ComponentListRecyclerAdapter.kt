package com.bprit.app.bprit.models

import android.view.LayoutInflater
import android.view.ViewGroup
import io.realm.RealmResults
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bprit.app.bprit.R
import com.bprit.app.bprit.data.RealmComponent
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.interfaces.ComponentListRecyclerViewOnClickListener
import com.bprit.app.bprit.interfaces.ComponentTypeListRecyclerViewOnClickListener
import io.realm.Case
import io.realm.Realm
import io.realm.Sort
import android.provider.ContactsContract.CommonDataKinds.Email
import io.realm.RealmQuery
import java.lang.Exception


/**
 * Component list recycler adaptor.
 * @param componentListRecyclerViewOnClickListener cell on click listener.
 * @param componentTypeId id of component type.
 */
class ComponentListRecyclerAdapter(
    private val componentListRecyclerViewOnClickListener: ComponentListRecyclerViewOnClickListener,
    private val componentTypeId: Int
) : RecyclerView.Adapter<ComponentListRecyclerAdapter.ViewHolder>() {

    private var realm: Realm? = null
    private var realmComponentRealmResults: RealmResults<RealmComponent>? = null

    /**
     * View holder for adaptor.
     * @param view view for the viewholder.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var v: View = view
        var serialTextView: TextView = view.findViewById(R.id.serialTextView)

        fun bindOnClickListener(id: Int?, listener: ComponentListRecyclerViewOnClickListener) {
            v.setOnClickListener {
                listener.onClick(v, id)
            }
        }

        init {
        }
    }

    init {
    }

    /**
     * Filter list of cells in adaptor by string.
     * @param filter only show cells which contains this filter.
     */
    fun filterList(filter: String) {
        val query = realm?.where(RealmComponent::class.java)
        query?.equalTo("typeId", componentTypeId)
            ?.contains("id", filter)
            ?.equalTo("isDeleted", false)

        val fieldNames = arrayOf("id")
        val sortOrders = arrayOf(Sort.ASCENDING)
        realmComponentRealmResults = query?.sort(fieldNames, sortOrders)?.findAll()

        notifyDataSetChanged()
    }

    /**
     * Method called when creating viewholder.
     * @param parent group of view.
     * @param viewType type of view, useful if several types of view are displayed in the same list.
     * @return the view holder created.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentListRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_component, parent, false)
        return ViewHolder(view)
    }

    /**
     * Method called when binding viewholder to recyclerview.
     * @param holder the recycled viewholder to be binded.
     * @param position on the recyclerview list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        realmComponentRealmResults?.let { results ->
            if (position < results.size) {
                val realmComponentType = results[position]

                realmComponentType?.let { ct ->
                    if (ct.id != null) {
                        holder.serialTextView.text = ct.id?.toString()
                    } else {
                        holder.serialTextView.text = ""
                    }

                    holder.bindOnClickListener(ct.id?.toInt(), componentListRecyclerViewOnClickListener)
                }
            }
        }
    }

    /**
     * Method called when adaptor is requesting the number of items to be loaded.
     * @return number of items to be loaded.
     */
    override fun getItemCount(): Int {
        realmComponentRealmResults?.let { results ->
            return results.size
        }

        return 0
    }

    /**
     * Method called when adaptor is attached to recyclerview.
     * Open connection to local database here.
     * @param recyclerView the recyclerview the adaptor is being attached to.
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        realm = Realm.getDefaultInstance()

        val query = realm?.where(RealmComponent::class.java)
        query?.equalTo("typeId", componentTypeId)
            ?.equalTo("isDeleted", false)

        val fieldNames = arrayOf("id")
        val sortOrders = arrayOf(Sort.ASCENDING)
        realmComponentRealmResults = query?.sort(fieldNames, sortOrders)?.findAll()
    }

    /**
     * Method called when adaptor is detached from recyclerview.
     * Close connection to local database here.
     * @param recyclerView the recyclerview the adaptor is detached from.
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        realm?.close()
    }
}