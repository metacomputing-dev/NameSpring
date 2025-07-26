package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager

class NavigationHeaderUI(
    private val lifecycleOwner: LifecycleOwner,
    private val navigationView: NavigationView
) {
    private val headerView = navigationView.getHeaderView(0)
    private lateinit var tvTitle: TextView
    private lateinit var tvFullName: TextView
    private lateinit var tvBirthInfo: TextView


    fun initView() {
        tvTitle = headerView.findViewById(R.id.navigation_header_curr_profile_title)
        tvFullName = headerView.findViewById(R.id.navigation_header_tv_fullname)
        tvBirthInfo = headerView.findViewById(R.id.navigation_header_tv_birth)

        ProfileManager.currentProfileId.observe(lifecycleOwner) {
            updateHeader()
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    fun updateHeader() {
        ProfileManager.currentProfile?.apply {
            tvTitle.text = title.value
            tvFullName.text =
                if (fullNameHanja.isNotEmpty()) "$fullName($fullNameHanja)"
                else fullName

            tvBirthInfo.text = birthAsPrettyString
        }
    }
}