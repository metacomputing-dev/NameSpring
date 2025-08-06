package com.metacomputing.namespring.control

import android.util.Log
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.namespring.ui.utils.AndroidLogger
import com.metacomputing.namespring.utils.getHanjaAt
import com.metacomputing.seed.Seed
import com.metacomputing.seed.model.HanjaSearchResult
import java.util.Calendar


object SeedProxy {
    private const val TAG = "SeedProxy"
    private var seed: Seed? = null
    var initialized: Boolean = false
        private set

    fun initialize() {
        Log.i(TAG, "Initialize SeedEngine")
        seed = Seed(AndroidLogger())
        Log.i(TAG, "SeedEngine initialization done.")
        initialized = true
    }

    fun makeNamingReport(profile: Profile): ArrayList<NamingReport> {
        with (profile) {
            val reports = ArrayList<NamingReport>()
            val namingQuery = buildNamingQuery(fullName, fullNameHanja)
            Log.i(TAG, "Created Query from ${fullName}(${fullNameHanja}) to $namingQuery ")
            Log.i(TAG, "Running Seed with profile $profile")

            birthDate.value?.run {
                val results = if (isCompleteQuery(namingQuery)) {
                    arrayListOf(
                        seed?.evaluateName(
                            surname = familyName.value!!,
                            surnameHanja = familyNameHanja.value!!,
                            givenName = firstName.value!!,
                            givenNameHanja = firstNameHanja.value!!,
                            year = get(Calendar.YEAR),
                            month = get(Calendar.MONTH),
                            day = get(Calendar.DAY_OF_MONTH),
                            hour = get(Calendar.HOUR_OF_DAY),
                            minute = get(Calendar.MINUTE),
                            targetGender = if (gender.value == Profile.Companion.Gender.FEMALE) "FM" else "M"
                        )
                    )
                } else {
                    seed?.searchNames(
                        query = namingQuery,
                        year = get(Calendar.YEAR),
                        month = get(Calendar.MONTH),
                        day = get(Calendar.DAY_OF_MONTH),
                        hour = get(Calendar.HOUR_OF_DAY),
                        minute = get(Calendar.MINUTE),
                        limit = 10000000
                    )
                }

                results?.forEach { result ->
                    result?.let {
                        reports.add(NamingReport.build(it))
                    }
                }
            }
            return reports
        }
    }

    private fun buildNamingQuery(name: String, hanja: String): String {
        var ret = ""
        name.forEachIndexed { index, letter ->
            ret += "[$letter/${hanja.getHanjaAt(index)}]"
        }
        return ret
    }

    private fun isCompleteQuery(query: String): Boolean {
        return !query.contains("_")
    }

    fun getHanjaInfoByPronounce(pronounce: String): List<HanjaSearchResult> {
        return seed?.searchHanjaByKorean(pronounce) ?: ArrayList()
    }
}