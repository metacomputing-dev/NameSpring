package com.metacomputing.namespring.control

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.model.viewmodel.Profile
import java.util.Calendar

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

    // TODO for debugging.
    fun putMockup(context: Context) {
        with (profiles.value) {
            add(Profile
                .new(context,
                    "DebugProfile: 최성수",
                    birthDate = Calendar.getInstance().apply { set(1986, 4, 19, 5, 45) },
                    gender = Profile.Companion.Gender.MALE,
                    firstName = "성수",
                    firstNameHanja = "成秀",
                    familyName = "최",
                    familyNameHanja = "崔",
                )
            )
            add(Profile
                .new(context,
                    "DebugProfile: 김우현",
                    birthDate = Calendar.getInstance().apply { set(1989, 1, 10, 1, 30) },
                    gender = Profile.Companion.Gender.MALE,
                    firstName = "우현",
                    firstNameHanja = "禹鉉",
                    familyName = "김",
                    familyNameHanja = "金",
                ), true
            )
        }
    }
}