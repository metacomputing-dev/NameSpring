package com.metacomputing.namespring.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
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
                    NamingRequestDialog.show(requireContext(), this@run) { reports ->
                        openNamingReportFragment(reports)
                    }
                }
            }
        }
    }

    private fun openNamingReportFragment(report: ArrayList<NamingReport>) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, NamingListFragment(report))
            .addToBackStack(null)
            .commit()
    }

}