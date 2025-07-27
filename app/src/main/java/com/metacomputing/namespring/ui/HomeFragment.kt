package com.metacomputing.namespring.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.viewmodel.getHanjaAt
import com.metacomputing.namespring.ui.util.HanjaSearchDialogUtil

class HomeFragment: BaseFragment() {
    companion object {
        const val TAG = "HomeFragment"
    }
    private lateinit var layout: View
    private lateinit var namingCard: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = inflater.inflate(R.layout.fragment_home, container, false)
        layout.post {
            initView()
        }
        return layout
    }

    private fun initView() {
        namingCard = layout.findViewById(R.id.service_cardview_naming)
        var familyCharText: EditText? = null
        var familyHanjaTextView: TextView? = null
        val namesText = ArrayList<EditText>()
        val namesHanjaText = ArrayList<TextView>()
        namingCard.setOnClickListener {
            ProfileManager.currentProfile?.run {
                showDialog(
                    R.string.naming_start,
                    R.layout.naming_request_form,
                    R.string.naming_start,
                    onCreateLayout = { layout ->
                        familyCharText = layout.findViewById(R.id.profile_form_family_text)
                        val familyHanjaCardView: CardView = layout.findViewById(R.id.profile_edit_family_hanja_cardview)
                        familyHanjaTextView = layout.findViewById(R.id.profile_edit_family_hanja_text)
                        val nameContainer: LinearLayout = layout.findViewById(R.id.naming_request_name_container)

                        familyCharText?.setText(familyName.value.toString())
                        familyHanjaTextView?.text = familyNameHanja.value?.getHanjaAt(0)
                        familyHanjaCardView.setOnClickListener {
                            HanjaSearchDialogUtil.show(this@HomeFragment,
                                pronounce = familyCharText?.text.toString(),
                                currentHanja = familyHanjaTextView?.text.toString()) { hanjaInfo ->
                                familyHanjaTextView?.text = hanjaInfo.hanja
                            }
                        }
                        firstName.value?.forEachIndexed { idx, char ->
                            View.inflate(requireContext(), R.layout.item_name_first, null).run {
                                post {
                                    val name = findViewById<EditText>(R.id.profile_edit_char_text)
                                    val nameHanjaCardView: CardView = findViewById(R.id.profile_edit_hanja_cardview)
                                    val nameHanja: TextView = findViewById(R.id.profile_edit_hanja_text)

                                    namesText.add(name)
                                    namesHanjaText.add(nameHanja)

                                    name.setText(char.toString())
                                    nameHanja.text = firstNameHanja.value?.getHanjaAt(idx)
                                    nameHanjaCardView.setOnClickListener {
                                        HanjaSearchDialogUtil.show(this@HomeFragment,
                                            pronounce = name.text.toString(),
                                            currentHanja = nameHanja.text.toString()) { hanjaInfo ->
                                            nameHanja.text = hanjaInfo.hanja
                                        }
                                    }
                                }
                                nameContainer.addView(this)
                            }
                        }
                    },
                    onPressedOk = { layout ->
                        var requiredName: String = familyName.value ?: ""
                        layout.findViewById<LinearLayout>(R.id.naming_request_name_container).run {
                            // TODO currently there's no limitation of the length of the naming
                            traverseViewTree(this) { view ->
                                if (view.id == R.id.profile_edit_char_text) {
                                    requiredName += (view as EditText).text.toString()
                                }
                            }
                        }

                        val profileForNaming = this.deepCopy()
                        profileForNaming.familyName.value = familyCharText?.text.toString()
                        profileForNaming.familyNameHanja.value = familyHanjaTextView?.text.toString()

                        var firstName = ""
                        namesText.forEach { firstName += it.text }
                        var firstNameHanja = ""
                        namesHanjaText.forEach { firstNameHanja += it.text }

                        profileForNaming.firstName.value = firstName
                        profileForNaming.firstNameHanja.value = firstNameHanja
                        Log.i(TAG, "Make Naming Report on Profile: $profileForNaming")

                        TaskManager.launch(requireContext(),
                            "Make Naming Profile $profileForNaming",
                            block = {
                                val reports = SeedProxy.makeNamingReport(profileForNaming)
                                Log.i(TAG, "Created Naming reports (size=${reports.size})")
                                if (reports.size > 30) {
                                    // TODO too many items. cut for now
                                    reports.subList(30, reports.size).clear()
                                }
                                // TODO add loading UI
                                openNamingReportFragment(reports)
                            })
                    })
            }
        }
    }

    private fun openNamingReportFragment(report: ArrayList<NamingReport>) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, NamingListFragment(report))
            .addToBackStack(null)
            .commit()
    }

    fun traverseViewTree(view: View, visitor: (View) -> Unit) {
        visitor(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                traverseViewTree(view.getChildAt(i), visitor)
            }
        }
    }
}