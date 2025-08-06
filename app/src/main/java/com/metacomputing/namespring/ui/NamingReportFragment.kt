package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.databinding.FragmentNamingReportBinding
import com.metacomputing.namespring.databinding.ListItemNamingReportCommonBinding
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.viewmodel.Profile

class NamingReportFragment(
    private val report: NamingReport
): BaseFragment() {
    private lateinit var binding: FragmentNamingReportBinding

    private inner class NamingReportAdapter : RecyclerView.Adapter<NamingReportAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ListItemNamingReportCommonBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListItemNamingReportCommonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount() = report.reportItems.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with (holder.binding) {
                namingReportItemTitle.text = report.reportItems[position].title
                namingReportItemDescription.text = report.reportItems[position].desc
                namingReportItemDetails.text = report.reportItems[position].details
                namingReportItemScore.text = report.reportItems[position].scoreString
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentNamingReportBinding.inflate(LayoutInflater.from(requireContext())).apply {
            binding = this
            namingReportRecyclerView.apply {
                layoutManager = LinearLayoutManager(inflater.context)
                adapter = NamingReportAdapter()
            }
            ProfileManager.mainProfile?.clone()?.run {
                namingReportHeaderTextProfileSummary.text =
                    "${report.name}(${report.hanja}) ${gender.value} ($birthAsString)"
            }
            namingReportHeaderTextScore.text = "${report.totalScore}점"

            return root
        }
    }
}