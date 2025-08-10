package com.metacomputing.namespring.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.databinding.FragmentHomeBinding
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.ui.utils.NamingRequestDialog

class HomeFragment: BaseFragment() {
    companion object {
        const val TAG = "HomeFragment"
    }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireContext()))
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            serviceCardviewNaming.setOnClickListener {
                ProfileManager.mainProfile?.run {
                    NamingRequestDialog.show(requireContext(), this@run) { query, reports ->
                        when (reports.size) {
                            0 -> {
                                Log.w(TAG, "No reports found from query=$query")
                                Toast.makeText(context, resources.getString(R.string.naming_report_empty_message), Toast.LENGTH_LONG).show()
                            }
                            1 -> openNamingReportFragment(reports[0])
                            else -> openNamingReportListFragment(reports)
                        }
                    }
                }
            }
            serviceCardviewReport.setOnClickListener {
                ProfileManager.mainProfile?.run {
                    val report = SeedProxy.makeNamingReport(this)
                    if (report.size == 1) {
                        openNamingReportFragment(report[0])
                    } else throw RuntimeException("invalid reports from Seed. (report count=${report.size})")
                }
            }
        }
    }

    private fun openNamingReportListFragment(report: ArrayList<NamingReport>) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, NamingListFragment(report))
            .addToBackStack(null)
            .commit()
    }

    private fun openNamingReportFragment(report: NamingReport) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, NamingReportFragment(report))
            .addToBackStack(null)
            .commit()
    }
}