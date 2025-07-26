package com.metacomputing.namespring.control

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.model.viewmodel.Profile
import java.util.Calendar

object ProfileManager {
    private const val TAG = "ProfileManager"
    val profiles: ArrayList<Profile> = ArrayList()
    var currentId: String? = null
        set(value) {
            field = value
            currentProfileId.value = value
        }
        get() = currentProfileId.value
    var currentProfile: Profile? = null
        get() {
            return try {
                currentId?.let { getById(it) }
            } catch (e: NoSuchElementException) {
                null
            }
        }
        private set

    val currentProfileId = MutableLiveData<String>()

    fun setCurrent(profile: Profile) {
        if (profiles.contains(profile)) {
            currentId = profile.id
            currentProfile = profile
        } else {
            Log.e(TAG, "No such profile.")
        }
    }

    fun add(profile: Profile) {
        profiles.add(profile)
        currentId = profile.id
    }

    fun getByName(fullName: String): List<Profile> {
        return profiles.filter { profile -> profile.fullName == fullName }
    }

    fun getByTitle(title: String): List<Profile> {
        return profiles.filter { profile -> profile.title.value == title }
    }

    fun getById(id: String): Profile {
        return profiles.first { profile -> profile.id == id }
    }

    fun remove(profile: Profile) {
        if (profile.id == currentId) {
            currentId = null
            currentProfile = null
        }
        profiles.remove(profile)
    }

    // TODO temporal function for debugging in dev phase
    fun initByMock(context: Context) {
        if (profiles.size == 0) {
            for(i in 0 ..< 15) {
                add(Profile.new(context, "profile title $i",
                    birthDate = Calendar.getInstance(),
                    gender = "남",
                    firstName = "우현",
                    firstNameHanja = "禹鉉",
                    familyName = "김",
                    familyNameHanja = "金"))
            }
        }
    }
}