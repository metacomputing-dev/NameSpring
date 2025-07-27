package com.metacomputing.namespring.control

import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.seed.Seed
import com.metacomputing.seed.model.HanjaSearchResult
import java.util.Calendar


object SeedProxy {
    private const val TAG = "SeedProxy"

    fun makeNamingReport(profile: Profile): ArrayList<NamingReport> {
        val reports = ArrayList<NamingReport>()
        val namingQuery = buildFormattedNameString(profile.fullName, profile.fullNameHanja)
        profile.birthDate.value?.run {
            val results = Seed.searchNames(
                query = namingQuery,
                year = get(Calendar.YEAR),
                month = get(Calendar.MONTH),
                day = get(Calendar.DAY_OF_MONTH),
                hour = get(Calendar.HOUR_OF_DAY),
                minute = get(Calendar.MINUTE),
                limit = 10000000
            )
            results.run {
                results.forEach {
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
        }
        return reports
    }

    private fun buildFormattedNameString(name: String, hanja: String): String {
        fun toFormat(char: Char?): String {
            return char?.toString() ?: "_"
        }
        name.toList().run {
            return "[${toFormat(name[0])}/${toFormat(hanja[0])}]" +
                    "[${toFormat(name[1])}/${toFormat(hanja[1])}]" +
                    "[${toFormat(name[2])}/${toFormat(hanja[2])}]"
        }
    }

    fun getHanjaInfoByPronounce(pronounce: String): List<HanjaSearchResult> {
        return Seed.searchHanjaByKorean(pronounce)
    }
}