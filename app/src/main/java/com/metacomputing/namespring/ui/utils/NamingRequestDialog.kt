package com.metacomputing.namespring.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.databinding.NamingRequestFormBinding
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.namespring.utils.getHanjaAt

object NamingRequestDialog {
    private const val TAG = "NamingRequestDialog"

    @SuppressLint("SetTextI18n")
    fun show(context: Context, profile: Profile, onNamingResult: (ArrayList<NamingReport>) -> Unit) {
        val binding = NamingRequestFormBinding.inflate(LayoutInflater.from(context))
        val namesText = ArrayList<EditText>()
        val namesHanjaText = ArrayList<TextView>()

        with (binding) {
            profile.run {
                ViewUtils.showDialog(context,
                    R.string.naming_start,
                    binding.root,
                    onCreateLayout = { _ ->
                        with (familyNameContainer) {
                            profileFormFamilyText.setText(familyName.value.toString())
                            profileEditFamilyHanjaText.text = familyNameHanja.value?.getHanjaAt(0)
                            profileEditFamilyHanjaCardview.setOnClickListener {
                                HanjaSearchDialog.show(context,
                                    pronounce = profileFormFamilyText.text.toString(),
                                    currentHanja = profileEditFamilyHanjaText.text.toString()) { hanjaInfo ->
                                    profileEditFamilyHanjaText.text = hanjaInfo.hanja
                                }
                            }

                            firstName.value?.forEachIndexed { idx, char ->
                                NameSlot(context, char.toString(), firstNameHanja.value?.getHanjaAt(idx)).binding.run {
                                    namesText.add(profileEditCharText)
                                    namesHanjaText.add(profileEditHanjaText)
                                    namingRequestNameContainer.addView(root)
                                }
                            }
                        }
                    },
                    onPressedOk = { _ ->
                        var requiredName: String = familyName.value ?: ""

                        with (namingRequestNameContainer) {
                            // TODO currently there's no limitation of the length of the naming
                            traverseViewTree(this) { view ->
                                if (view.id == R.id.profile_edit_char_text) {
                                    requiredName += (view as EditText).text.toString()
                                }
                            }
                        }

                        with (familyNameContainer) {
                            val profileForNaming = this@run.clone().apply {
                                familyName.value = profileFormFamilyText.text.toString()
                                familyNameHanja.value = profileEditFamilyHanjaText.text.toString()

                                namesHanjaText.forEach { firstNameHanja.value += it.text }
                                namesText.forEach { firstName.value += it.text }
                            }
                            Log.i(TAG, "Make Naming Report on Profile: $profileForNaming")

                            TaskManager.launch(context,
                                "Make Naming Profile $profileForNaming",
                                block = {
                                    val reports = SeedProxy.makeNamingReport(profileForNaming)
                                    Log.i(TAG, "Created Naming reports (size=${reports.size})")
                                    if (reports.size > 30) {
                                        // TODO too many items. cut for now
                                        reports.subList(30, reports.size).clear()
                                    }
                                    // TODO add loading UI
                                    onNamingResult.invoke(reports)
                                })
                        }
                    })
            }
        }
    }
}