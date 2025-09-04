package com.metacomputing.namespring.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.databinding.NamingRequestFormBinding
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.namespring.utils.emptyIfUnderscore
import com.metacomputing.namespring.utils.getHanjaAt
import com.metacomputing.namespring.utils.underscoreIfEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NamingRequestDialog {
    private const val TAG = "NamingRequestDialog"

    @SuppressLint("SetTextI18n")
    fun show(context: Context, profile: Profile, onNamingResult: (query: String, ArrayList<NamingReport>) -> Unit) {
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
                            profileFormFamilyText.apply {
                                setText(familyName.emptyIfUnderscore())
                                doOnTextChanged { _, _, _, _ ->
                                    profileEditFamilyHanjaText.text = ""
                                }
                            }
                            profileEditFamilyHanjaText.text = familyNameHanja.getHanjaAt(0)?.emptyIfUnderscore()
                            profileEditFamilyHanjaCardview.setOnClickListener {
                                if (profileEditFamilyHanjaText.text.isEmpty()) return@setOnClickListener

                                HanjaSearchDialog.show(context,
                                    pronounce = profileFormFamilyText.text.toString(),
                                    currentHanja = profileEditFamilyHanjaText.text.toString()) { hanjaInfo ->
                                    profileEditFamilyHanjaText.text = hanjaInfo?.hanja ?: ""
                                }
                            }

                            firstName.forEachIndexed { idx, char ->
                                NameSlot(context, char.toString(), firstNameHanja.getHanjaAt(idx)).binding.run {
                                    namesText.add(profileEditCharText)
                                    namesHanjaText.add(profileEditHanjaText)
                                    namingRequestNameContainer.addView(root)
                                }
                            }
                        }
                    },
                    onPressedOk = { _ ->
                        var requiredName: String = familyName

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
                                familyName = profileFormFamilyText.text.underscoreIfEmpty()
                                familyNameHanja = profileEditFamilyHanjaText.text.underscoreIfEmpty()
                                firstName = ""
                                firstNameHanja = ""
                                namesText.forEach { firstName += it.text.underscoreIfEmpty() }
                                namesHanjaText.forEach { firstNameHanja += it.text.underscoreIfEmpty() }
                            }
                            Log.i(TAG, "Make Naming Report on Profile: $profileForNaming")

                            TaskManager.launch(
                                "Make Naming Profile $profileForNaming",
                                block = {
                                    val reports = SeedProxy.makeNamingReport(profileForNaming)
                                    Log.i(TAG, "Created Naming reports (size=${reports.size})")
                                    if (reports.size > 30) {
                                        // TODO too many items. cut for now
                                        reports.subList(30, reports.size).clear()
                                    }
                                    CoroutineScope(Dispatchers.Main).launch {
                                        onNamingResult.invoke(profileForNaming.buildNamingQuery(), reports)
                                    }
                                }
                            )
                        }
                    })
            }
        }
    }
}