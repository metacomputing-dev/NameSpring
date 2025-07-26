package com.metacomputing.namespring.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import com.metacomputing.namespring.R
import com.metacomputing.namespring.model.viewmodel.Profile
import java.util.Calendar

class ProfileEditFragment(
    private val profile: Profile
): BaseFragment() {
    private lateinit var layout: View

    private lateinit var title: EditText

    private lateinit var nameContainer: LinearLayout
    private lateinit var family: EditText
    private var names = ArrayList<EditText>()

    private lateinit var year: EditText
    private lateinit var month: EditText
    private lateinit var day: EditText

    private lateinit var hour: EditText
    private lateinit var minutes: EditText

    private lateinit var genderGroup: RadioGroup

    private lateinit var save: AppCompatButton
    private lateinit var cancel: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        layout.run {
            post {
                title = findViewById(R.id.profile_form_title_text)

                nameContainer = findViewById(R.id.profile_edit_name_container)
                family = findViewById(R.id.profile_form_family_text)

                year = findViewById(R.id.profile_form_year)
                month = findViewById(R.id.profile_form_month)
                day = findViewById(R.id.profile_form_day)

                hour = findViewById(R.id.profile_form_hour)
                minutes = findViewById(R.id.profile_form_minutes)

                genderGroup = findViewById(R.id.radio_group_gender)

                save = findViewById(R.id.save)
                save.setOnClickListener {
                    saveProcess()
                    moveBackFragment()
                }
                cancel = findViewById(R.id.cancel)
                cancel.setOnClickListener {
                    moveBackFragment()
                }

                loadFrom(profile)
            }
        }
        return layout
    }

    private fun loadFrom(profile: Profile) {
        title.setText(profile.title.value)
        family.setText(profile.familyName.value)
        profile.firstName.value?.forEach { char ->
            View.inflate(requireContext(), R.layout.item_name_first, null).run {
                post {
                    // TODO hanja info
                    val name = findViewById<EditText>(R.id.profile_edit_char_text)
                    name.setText(char.toString())
                    names.add(name)
                }
                nameContainer.addView(this)
            }
        }

        year.setText(profile.getBirthDateOf(Calendar.YEAR).toString())
        month.setText(profile.getBirthDateOf(Calendar.MONTH).toString())
        day.setText(profile.getBirthDateOf(Calendar.DAY_OF_MONTH).toString())

        hour.setText(profile.getBirthDateOf(Calendar.HOUR_OF_DAY).toString())
        minutes.setText(profile.getBirthDateOf(Calendar.MINUTE).toString())

        when (profile.gender.value) {
            Profile.Companion.Gender.MALE -> genderGroup.check(R.id.radio_gender_male)
            Profile.Companion.Gender.FEMALE -> genderGroup.check(R.id.radio_gender_female)
        }
    }

    private fun saveProcess() {
        // TODO save to DataStore in future
        profile.title.value = title.text.toString()
        profile.familyName.value = family.text.toString()
        var firstName = ""
        names.forEach { editText ->
            firstName += editText.text.toString()
        }
        profile.firstName.value = firstName
        profile.birthDate.value = getBirthDate()
        profile.gender.value = getGender()
    }

    @Profile.Companion.Gender
    private fun getGender(): String? {
        return when (genderGroup.checkedRadioButtonId) {
            R.id.radio_gender_male -> Profile.Companion.Gender.MALE
            R.id.radio_gender_female -> Profile.Companion.Gender.FEMALE
            else -> null
        }
    }

    private fun getBirthDate(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year.text.toString().toInt())
            set(Calendar.MONTH, month.text.toString().toInt())
            set(Calendar.DAY_OF_MONTH, day.text.toString().toInt())
            set(Calendar.HOUR_OF_DAY, hour.text.toString().toInt())
            set(Calendar.MINUTE, minutes.text.toString().toInt())
        }
    }
    private fun moveBackFragment() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}