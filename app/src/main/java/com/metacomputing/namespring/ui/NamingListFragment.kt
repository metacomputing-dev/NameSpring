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
import com.metacomputing.namespring.model.viewmodel.NamingReport

class NamingListFragment(
    private val reports: ArrayList<NamingReport>
): BaseFragment() {
    private lateinit var layout: View
    private lateinit var recyclerView: RecyclerView

    inner class NamingListAdapter(
        private val items: MutableList<NamingReport>,
    ) : RecyclerView.Adapter<NamingListAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardView: CardView = view.findViewById(R.id.naming_list_item_cardview)
            val nameView: TextView = view.findViewById(R.id.naming_list_item_name)
            val scoreView: TextView = view.findViewById(R.id.naming_list_item_score)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.naming_list_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = items.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cardView.setTag(R.id.tag_key_naming_list_item, items[position])
            holder.cardView.setOnClickListener {
                holder.cardView.getTag(R.id.tag_key_naming_list_item)?.let {
                    openNamingReportFragment(it as NamingReport)
                }
            }
            holder.nameView.text = items[position].name
            holder.scoreView.text = items[position].totalScore.toString()
        }

        private fun openNamingReportFragment(report: NamingReport) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, NamingReportFragment(report))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = inflater.inflate(R.layout.fragment_naming_list, container, false)
        layout.post {
            recyclerView = layout.findViewById(R.id.naming_list_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(inflater.context)
            recyclerView.adapter = NamingListAdapter(reports)
        }

        return layout
    }
}