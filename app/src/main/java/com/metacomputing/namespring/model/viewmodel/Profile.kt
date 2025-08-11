package com.metacomputing.namespring.model.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringDef
import androidx.lifecycle.MutableLiveData
import com.metacomputing.namespring.utils.emptyIfUnderscore
import com.metacomputing.namespring.utils.getHanjaAt
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class Profile(
    val title: MutableLiveData<String>,
    val locale: MutableLiveData<Locale>,
    val birthDate: MutableLiveData<Calendar>,
    @Gender val gender: MutableLiveData<String>,
    val firstName: MutableLiveData<String>,
    val firstNameHanja: MutableLiveData<String>,
    val familyName: MutableLiveData<String>,
    val familyNameHanja: MutableLiveData<String>,
    val id: String = UUID.randomUUID().toString()
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
            title: String?,
            locale: Locale?,
            birthDate: Calendar?,
            @Gender gender: String?,
            firstName: String?,
            firstNameHanja: String?,
            familyName: String?,
            familyNameHanja: String?,
            id: String
        ): Profile {
            return Profile(
                MutableLiveData<String>(title),
                MutableLiveData<Locale>(locale),
                MutableLiveData<Calendar>(birthDate),
                MutableLiveData<String>(gender),
                MutableLiveData<String>(firstName),
                MutableLiveData<String>(firstNameHanja),
                MutableLiveData<String>(familyName),
                MutableLiveData<String>(familyNameHanja),
                id
            )
        }

        fun new(
            title: String?,
            locale: Locale?,
            birthDate: Calendar?,
            @Gender gender: String?,
            firstName: String?,
            firstNameHanja: String?,
            familyName: String?,
            familyNameHanja: String?
        ): Profile {
            return Profile(
                MutableLiveData<String>(title),
                MutableLiveData<Locale>(locale),
                MutableLiveData<Calendar>(birthDate),
                MutableLiveData<String>(gender),
                MutableLiveData<String>(firstName),
                MutableLiveData<String>(firstNameHanja),
                MutableLiveData<String>(familyName),
                MutableLiveData<String>(familyNameHanja)
            )
        }

        fun new(context: Context,
                title: String? = "New Profile",
                birthDate: Calendar = Calendar.getInstance(),
                @Gender gender: String = Gender.MALE,
                firstName: String = "",
                firstNameHanja: String = "",
                familyName: String = "",
                familyNameHanja: String = ""
        ): Profile {
            val loc = context.resources.configuration.locales.get(0)
            return new(title, loc, birthDate, gender, firstName, firstNameHanja, familyName, familyNameHanja)
        }
    }

    // Sugar syntax
    val fullName: String
        get() = familyName.value + firstName.value
    val fullNameHanja: String
        get() = familyNameHanja.value + firstNameHanja.value

    val fullNamePrettyString: String
        get() = familyName.value?.emptyIfUnderscore() + firstName.value?.emptyIfUnderscore()

    val fullNameHanjaPrettyString: String
        get() = familyNameHanja.value?.emptyIfUnderscore() + firstNameHanja.value?.emptyIfUnderscore()

    val birthAsString: String
        get() {
            return birthDate.value?.run {
                "" + get(Calendar.YEAR) + "." + get(Calendar.MONTH) + "." + get(Calendar.DAY_OF_MONTH) + "."
            } ?: ""
        }

    val birthAsPrettyString: String
        @SuppressLint("DefaultLocale")
        get() {
            return birthDate.value?.run {
                String.format("%d년 %d월 %d일, %d시 %d분",
                    get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))
            } ?: ""
        }

    fun getBirthDateOf(fieldOfCalendar: Int): Int? {
        return birthDate.value?.get(fieldOfCalendar)
    }

    fun buildNamingQuery(): String {
        var ret = ""
        fullName.forEachIndexed { index, letter ->
            ret += "[$letter/${fullNameHanja.getHanjaAt(index)}]"
        }
        return ret
    }

    fun clone(): Profile {
        return new(
            title.value, locale.value, birthDate.value, gender.value,
            firstName.value, firstNameHanja.value, familyName.value, familyNameHanja.value)
    }

    override fun toString(): String {
        return "Profile{ id=${id}, title=${title.value}, locale=${locale.value}, birthDate=${birthAsString}, " +
                "gender=${gender.value}, firstName=${firstName.value}, firstNameHanja=${firstNameHanja.value}, " +
                "familyName=${familyName.value}, familyNameHanja=${familyNameHanja.value} }"
    }
}