package com.metacomputing.namespring.model.viewmodel

import android.content.Context
import androidx.annotation.StringDef
import androidx.lifecycle.MutableLiveData
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class Profile(
    val title: MutableLiveData<String>,
    val locale: MutableLiveData<Locale>,
    val birthDate: MutableLiveData<Calendar>,
    @Gender val gender: MutableLiveData<String>,
    val firstName: MutableLiveData<String>,
    val lastName: MutableLiveData<String>,
    ) {
    companion object {
        @StringDef(Gender.MALE, Gender.FEMALE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Gender {
            companion object {
                const val MALE = "남"
                const val FEMALE = "여"
            }
        }

        fun new(
            title: String,
            locale: Locale,
            birthDate: Calendar,
            @Gender gender: String,
            firstName: String,
            lastName: String
        ): Profile {
            return Profile(
                MutableLiveData<String>(title),
                MutableLiveData<Locale>(locale),
                MutableLiveData<Calendar>(birthDate),
                MutableLiveData<String>(gender),
                MutableLiveData<String>(firstName),
                MutableLiveData<String>(lastName)
            )
        }

        fun new(context: Context,
                title: String,
                birthDate: Calendar,
                @Gender gender: String,
                firstName: String,
                lastName: String
        ): Profile {
            val loc = context.resources.configuration.locales.get(0)
            return new(title, loc, birthDate, gender, firstName, lastName)
        }
    }

    val id: String = UUID.randomUUID().toString()
    val fullName: String
        get() = lastName.value + firstName.value

    val birthAsString: String
        get() {
            return birthDate.value?.run {
                "" + get(Calendar.YEAR) + "." + get(Calendar.MONTH) + "." + get(Calendar.DAY_OF_MONTH) + "."
            } ?: ""
        }

    fun getBirthDateOf(fieldOfCalendar: Int): Int? {
        return birthDate.value?.get(fieldOfCalendar)
    }

}