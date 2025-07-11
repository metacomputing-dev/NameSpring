package com.metacomputing.namespring.model.viewmodel

import androidx.lifecycle.LiveData
import java.util.Calendar
import java.util.Locale

data class Profile(
    var title: LiveData<String>,
    var locale: LiveData<Locale>,
    var birthDate: LiveData<Calendar>,
    var gender: LiveData<String>,
    var firstName: LiveData<String>,
    var lastName: LiveData<String>,
    ) {
    private val timestamp: Long = System.currentTimeMillis()

    val id: Long
        get() = timestamp
    val fullName: String
        get() = firstName.value + lastName.value
}