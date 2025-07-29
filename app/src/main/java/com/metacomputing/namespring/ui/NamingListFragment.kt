package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.databinding.FragmentNamingListBinding
import com.metacomputing.namespring.databinding.ListItemNamingResultBinding
import com.metacomputing.namespring.model.report.NamingReport

class NamingListFragment(
    private val reports: ArrayList<NamingReport>
): BaseFragment() {
    private lateinit var binding: FragmentNamingListBinding

    private inner class NamingListAdapter(
        private val items: MutableList<NamingReport>,
    ) : RecyclerView.Adapter<NamingListAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ListItemNamingResultBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListItemNamingResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount() = items.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.apply {
                namingListItemCardview.apply {
                    setTag(R.id.tag_key_naming_list_item, items[position])
                    setOnClickListener {
                        getTag(R.id.tag_key_naming_list_item)?.let {
                            openNamingReportFragment(it as NamingReport)
                        }
                    }
                }
                namingListItemName.text = items[position].name
                namingListItemScore.text = items[position].totalScore.toString()
            }
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
        FragmentNamingListBinding.inflate(LayoutInflater.from(requireContext())).apply {
            binding = this
            namingListRecyclerView.apply {
                layoutManager = LinearLayoutManager(inflater.context)
                adapter = NamingListAdapter(reports)
            }
            return root
        }
    }
}