package com.metacomputing.namespring.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.metacomputing.namespring.R
import com.metacomputing.namespring.databinding.FragmentProfileEditBinding
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.namespring.ui.utils.HanjaSearchDialog
import com.metacomputing.namespring.ui.utils.NameSlot
import com.metacomputing.namespring.utils.emptyIfUnderscore
import com.metacomputing.namespring.utils.getHanjaAt
import com.metacomputing.namespring.utils.underscoreIfEmpty
import java.util.Calendar

class ProfileEditFragment(
    private val profile: Profile
): BaseFragment() {
    private lateinit var binding: FragmentProfileEditBinding

    private var names = ArrayList<EditText>()
    private var namesHanja = ArrayList<TextView>()

    private var nameLength = profile.firstName.value?.length ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(LayoutInflater.from(context)).apply {
            with (profileForm) {
                // Title
                profileFormTitleText.setText(profile.title.value)
                // Family Name
                familyName.apply {
                    profileFormFamilyText.apply {
                        setText(profile.familyName.value?.emptyIfUnderscore())
                        doOnTextChanged { _, _, _, _ ->
                            profileEditFamilyHanjaText.text = ""
                        }
                    }
                    profileEditFamilyHanjaText.text = profile.familyNameHanja.value?.emptyIfUnderscore()
                    profileEditFamilyHanjaCardview.setOnClickListener {
                        if (profileFormFamilyText.text.isEmpty()) return@setOnClickListener

                        HanjaSearchDialog.show(requireContext(),
                            pronounce = profileFormFamilyText.text.toString(),
                            currentHanja = profileEditFamilyHanjaText.text.toString()) { hanjaInfo ->
                            profileEditFamilyHanjaText.text = hanjaInfo?.hanja ?: ""
                        }
                    }
                }
                // Name
                profile.firstName.value?.forEachIndexed { idx, char ->
                    addNameSlot(profileEditNameContainer, char.toString(), idx)
                }

                // Birth
                profileFormYear.setText(profile.getBirthDateOf(Calendar.YEAR).toString())
                profileFormMonth.setText(profile.getBirthDateOf(Calendar.MONTH).toString())
                profileFormDay.setText(profile.getBirthDateOf(Calendar.DAY_OF_MONTH).toString())

                profileFormHour.setText(profile.getBirthDateOf(Calendar.HOUR_OF_DAY).toString())
                profileFormMinutes.setText(profile.getBirthDateOf(Calendar.MINUTE).toString())

                // Gender
                when (profile.gender.value) {
                    Profile.Companion.Gender.MALE -> radioGroupGender.check(R.id.radio_gender_male)
                    Profile.Companion.Gender.FEMALE -> radioGroupGender.check(R.id.radio_gender_female)
                }

                // Add Button
                btnAddChar.apply {
                    setOnClickListener {
                        if (nameLength <= 2) {
                            addNameSlot(profileEditNameContainer, null, nameLength)
                            nameLength++
                        }
                        updateSlotControlVisibility()
                    }
                }
                btnDeleteChar.apply {
                    setOnClickListener {
                        if (nameLength > 0) {
                            deleteNameSlot(profileEditNameContainer)
                            nameLength--
                        }
                        updateSlotControlVisibility()
                    }
                }

                save.setOnClickListener {
                    saveProcess()
                    moveBackFragment()
                }
                cancel.setOnClickListener {
                    moveBackFragment()
                }
            }
        }
        updateSlotControlVisibility()
        return binding.root
    }

    private fun updateSlotControlVisibility() {
        binding.profileForm.apply {
            btnAddChar.visibility = View.VISIBLE
            btnDeleteChar.visibility = View.VISIBLE
            if (nameLength <= 0) {
                btnDeleteChar.visibility = View.INVISIBLE
            }
            if (nameLength >= 3) {
                btnAddChar.visibility = View.INVISIBLE
            }
        }
    }
    private fun addNameSlot(slotContainer: LinearLayout, char: String?, idx: Int) {
        val slot = NameSlot(requireContext(), char, profile.firstNameHanja.value?.getHanjaAt(idx))
        with (slot.binding) {
            names.add(profileEditCharText)
            namesHanja.add(profileEditHanjaText)
            slotContainer.addView(root)
        }
    }

    private fun deleteNameSlot(slotContainer: LinearLayout) {
        slotContainer.apply {
            if (childCount > 1) {
                removeViewAt(childCount - 1)
            }
            names.removeAt(names.size - 1)
            namesHanja.removeAt(namesHanja.size - 1)
        }
    }

    private fun saveProcess() {
        // TODO save to DataStore in future
        with (profile) {
            title.value = binding.profileForm.profileFormTitleText.text.toString()

            familyName.value = binding.profileForm.familyName.profileFormFamilyText.text.underscoreIfEmpty()
            familyNameHanja.value = binding.profileForm.familyName.profileEditFamilyHanjaText.text.underscoreIfEmpty()
            names.forEach { v -> Log.e("KWH", "origin ${v.text} -> ${v.text.underscoreIfEmpty()}") }
            firstName.value = names.joinToString("") { v -> v.text.underscoreIfEmpty() }
            firstNameHanja.value = namesHanja.joinToString("") { v -> v.text.underscoreIfEmpty() }

            birthDate.value = getBirthDate()
            gender.value = getGender()
        }
    }

    @Profile.Companion.Gender
    private fun getGender(): String? {
        return when (binding.profileForm.radioGroupGender.checkedRadioButtonId) {
            R.id.radio_gender_male -> Profile.Companion.Gender.MALE
            R.id.radio_gender_female -> Profile.Companion.Gender.FEMALE
            else -> null
        }
    }

    private fun getBirthDate(): Calendar {
        return Calendar.getInstance().apply {
            with (binding.profileForm) {
                set(Calendar.YEAR, profileFormYear.text.toString().toInt())
                set(Calendar.MONTH, profileFormMonth.text.toString().toInt())
                set(Calendar.DAY_OF_MONTH, profileFormDay.text.toString().toInt())
                set(Calendar.HOUR_OF_DAY, profileFormHour.text.toString().toInt())
                set(Calendar.MINUTE, profileFormMinutes.text.toString().toInt())
            }
        }
    }

    private fun moveBackFragment() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}