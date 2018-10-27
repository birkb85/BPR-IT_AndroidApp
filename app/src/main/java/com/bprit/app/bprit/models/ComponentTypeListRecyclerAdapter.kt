package com.bprit.app.bprit.models

import java.nio.file.Files.size
import android.R.attr.description
import android.view.LayoutInflater
import android.view.ViewGroup
import io.realm.RealmResults
import android.R.attr.onClick
import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bprit.app.bprit.R
import com.bprit.app.bprit.data.RealmComponentType
import com.bprit.app.bprit.interfaces.ComponentTypeListRecyclerViewOnClickListener
import io.realm.Case
import io.realm.Realm
import io.realm.Sort


class ComponentTypeListRecyclerAdapter(
    private val componentTypeListRecyclerViewOnClickListener: ComponentTypeListRecyclerViewOnClickListener
) : RecyclerView.Adapter<ComponentTypeListRecyclerAdapter.ViewHolder>() {

    private var realm: Realm? = null
    private var realmComponentTypeRealmResults: RealmResults<RealmComponentType>? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var v = view
        var nameTextView: TextView = view.findViewById(R.id.nameTextView)
        var inStorageTextView: TextView = view.findViewById(R.id.inStorageTextView)

        fun bindOnClickListener(id: Int?, listener: ComponentTypeListRecyclerViewOnClickListener) {
            v.setOnClickListener {
                listener.onClick(v, id)
            }
        }

        init {
        }
    }

    init {
    }

    fun filterList(filter: String) {
        val fieldNames = arrayOf("name")
        val sortOrders = arrayOf(Sort.ASCENDING)
        realmComponentTypeRealmResults =
                realm?.where(RealmComponentType::class.java)?.contains("name", filter, Case.INSENSITIVE)
                    ?.equalTo("isDeleted", false)?.sort(fieldNames, sortOrders)?.findAll()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentTypeListRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_component_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        realmComponentTypeRealmResults?.let { results ->
            if (position < results.size) {
                val realmComponentType = results[position]

                realmComponentType?.let { ct ->
                    if (ct.name != null) {
                        holder.nameTextView.text = ct.name?.trim()
                    } else {
                        holder.nameTextView.text = ""
                    }

                    if (ct.inStorage != null) {
                        holder.inStorageTextView.text = ct.inStorage.toString().trim()
                    } else {
                        holder.inStorageTextView.text = ""
                    }

                    holder.bindOnClickListener(ct.id, componentTypeListRecyclerViewOnClickListener)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        realmComponentTypeRealmResults?.let { results ->
            return results.size
        }

        return 0
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        realm = Realm.getDefaultInstance()

        val fieldNames = arrayOf("name")
        val sortOrders = arrayOf(Sort.ASCENDING)
        realmComponentTypeRealmResults =
                realm?.where(RealmComponentType::class.java)?.equalTo("isDeleted", false)?.sort(fieldNames, sortOrders)
                    ?.findAll()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        realm?.close()
    }
}