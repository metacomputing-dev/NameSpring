package com.metacomputing.namespring.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.model.viewmodel.NamingReport

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
        namingCard.setOnClickListener {
            ProfileManager.currentProfile?.run {
                showDialog(
                    R.string.naming_start,
                    R.layout.naming_request_form,
                    R.string.naming_start,
                    onCreateLayout = { layout ->
                        val familyCharText: EditText = layout.findViewById(R.id.profile_form_family_text)
                        val nameContainer: LinearLayout = layout.findViewById(R.id.naming_request_name_container)

                        familyCharText.setText(familyName.value.toString())
                        firstName.value?.forEachIndexed { idx, char ->
                            View.inflate(requireContext(), R.layout.item_name_first, null).run {
                                post {
                                    // TODO hanja info
                                    val name = findViewById<EditText>(R.id.profile_edit_char_text)
                                    name.setText(char.toString())
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
                        TaskManager.launch(requireContext(),
                            "작명(프로필:${title.value}, 입력된 이름:${requiredName})",
                            block = {
                                val reports = SeedProxy.makeNamingReport(this)
                                Log.i(TAG, "Created Naming reports (${reports.size})")
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