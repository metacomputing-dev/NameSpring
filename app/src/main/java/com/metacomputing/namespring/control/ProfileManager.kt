package com.metacomputing.namespring.control

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.model.viewmodel.Profile

object ProfileManager {
    private const val TAG = "ProfileManager"
    val profiles = MutableLiveData(ArrayList<Profile>())
    val mainProfileId = MutableLiveData<String>()
    var mainProfile: Profile? = null
        get() {
            return try {
                mainProfileId.value?.let { getProfileById(it) }
            } catch (e: NoSuchElementException) {
                null
            }
        }
        set(value) {
            if (value == null ||
                (profiles.value?.contains(value) == true)) {
                mainProfileId.value = value?.id
                field = value
            } else {
                Log.e(TAG, "Tried to set main profile with invalid profile. skipping.")
            }
        }

    fun add(profile: Profile, setAsMain: Boolean = false) {
        profiles.value?.add(profile)
        if (setAsMain) mainProfile = profile
    }

    fun getProfileById(id: String): Profile? {
        return profiles.value?.first { profile -> profile.id == id }
    }

    fun remove(profile: Profile) {
        if (profile == mainProfile) {
            mainProfileId.value = null
        }
        profiles.value?.remove(profile)
    }
}