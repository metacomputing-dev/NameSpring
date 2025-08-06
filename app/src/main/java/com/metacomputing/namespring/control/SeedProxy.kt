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
        val reports = ArrayList<NamingReport>()
        val namingQuery = buildFormattedNameString(profile.fullName, profile.fullNameHanja)
        Log.i(TAG, "Created Query from ${profile.fullName}(${profile.fullNameHanja}) to $namingQuery ")
        Log.i(TAG, "Running Seed with params $namingQuery, ${profile.birthDate.value} ")

        profile.birthDate.value?.run {
            val results = seed?.searchNames(
                query = namingQuery,
                year = get(Calendar.YEAR),
                month = get(Calendar.MONTH),
                day = get(Calendar.DAY_OF_MONTH),
                hour = get(Calendar.HOUR_OF_DAY),
                minute = get(Calendar.MINUTE),
                limit = 10000000
            )
            results?.forEach {
                reports.add(
                    NamingReport(
                        it.fullName,
                        it.totalScore,
                        it.details.detailedScores.baleumOhaengScore.reason,
                        it.details.detailedScores.baleumEumYangScore.reason,
                        it.details.detailedScores.jawonOhaengScore.reason,
                        it.details.detailedScores.hoeksuEumYangScore.reason,
                        it.details.detailedScores.sajuNameOhaengScore.reason
                    )
                )
            }
        }
        return reports
    }

    private fun buildFormattedNameString(name: String, hanja: String): String {
        var ret = ""
        name.forEachIndexed { index, letter ->
            ret += "[$letter/${hanja.getHanjaAt(index)}]"
        }
        return ret
    }

    fun getHanjaInfoByPronounce(pronounce: String): List<HanjaSearchResult> {
        return seed?.searchHanjaByKorean(pronounce) ?: ArrayList()
    }
}