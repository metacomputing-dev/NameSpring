package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.databinding.NavigationHeaderBinding

class NavigationHeader(
    lifecycleOwner: LifecycleOwner,
    navigationView: NavigationView
) {
    private var binding = NavigationHeaderBinding.bind(navigationView.getHeaderView(0))

    init {
        ProfileManager.mainProfileId.observe(lifecycleOwner) {
            updateHeader()
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    fun updateHeader() {
        ProfileManager.mainProfile?.apply {
            with (binding) {
                navigationHeaderCurrProfileTitle.text = title.value
                navigationHeaderTvFullname.text =
                    if (fullNameHanja.isNotEmpty()) "$fullName($fullNameHanja)"
                    else fullName

                navigationHeaderTvBirth.text = birthAsPrettyString
            }
        }
    }
}