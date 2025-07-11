package com.metacomputing.namespring.control

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.metacomputing.namespring.R
import com.metacomputing.namespring.model.viewmodel.Profile

object ProfileManager {
    val profiles: ArrayList<Profile> = ArrayList()
    var currentId: Long? = null
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
    }

    fun getByName(fullName: String): List<Profile> {
        return profiles.filter { profile -> profile.fullName == fullName }
    }

    fun getByTitle(title: String): List<Profile> {
        return profiles.filter { profile -> profile.title.value == title }
    }

    fun getById(id: Long): Profile {
        return profiles.first { profile -> profile.id == currentId }
    }

    fun select(profile: Profile) {
        currentId = profile.id
    }

    fun showDialog(activity: Activity, profile: Profile? = null) {
        val form = LayoutInflater.from(activity).inflate(R.layout.profile_form, null)
        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.profile_new)
            .setView(form)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                // TODO run evaluation and save/update profile
                dialog.dismiss()
            }
            .setNegativeButton("취소", null)
            .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
    }
}