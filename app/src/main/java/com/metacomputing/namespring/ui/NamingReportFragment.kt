package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.model.report.NamingReport

class NamingReportFragment(
    private val report: NamingReport
): BaseFragment() {
    private lateinit var layout: View
    private lateinit var recyclerView: RecyclerView

    inner class NamingReportAdapter : RecyclerView.Adapter<NamingReportAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardView: CardView = view.findViewById(R.id.naming_report_item_cardview)
            val titleView: TextView = view.findViewById(R.id.naming_report_item_title)
            val descView: TextView = view.findViewById(R.id.naming_report_item_description)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_naming_report_common, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = report.reportItems.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.titleView.text = report.reportItems[position].title
            holder.descView.text = report.reportItems[position].desc
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = inflater.inflate(R.layout.fragment_naming_report, container, false)
        layout.post {
            recyclerView = layout.findViewById(R.id.naming_report_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(inflater.context)
            recyclerView.adapter = NamingReportAdapter()
        }

        return layout
    }
}