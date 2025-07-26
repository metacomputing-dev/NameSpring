package com.metacomputing.namespring.control

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.metacomputing.namespring.R
import com.metacomputing.namespring.model.viewmodel.Profile
import java.util.Calendar

object ProfileManager {
    val profiles: ArrayList<Profile> = ArrayList()
    var currentId: String? = null
    val currentProfile: Profile?
        get() {
            return try {
                currentId?.let { getById(it) }
            } catch (e: NoSuchElementException) {
                null
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

    fun select(profile: Profile) {
        currentId = profile.id
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