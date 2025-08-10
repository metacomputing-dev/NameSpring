package com.metacomputing.namespring.model.report

import com.metacomputing.namespring.model.metrics.BornComplElement
import com.metacomputing.namespring.model.metrics.PatternStroke
import com.metacomputing.namespring.model.metrics.PatternStrokeElement
import com.metacomputing.namespring.model.metrics.SoundBalance
import com.metacomputing.namespring.model.metrics.SoundElement
import com.metacomputing.namespring.model.metrics.Statistics
import com.metacomputing.namespring.model.metrics.StrokeBalance
import com.metacomputing.seed.model.NameEvaluationResult

data class NamingReport(
    val name: String,
    val hanja: String,
    val totalScore: Int,
    val overallReview: String,
    val statistics: Statistics,
    val patternStroke: PatternStroke,
    val patternStrokeElement: PatternStrokeElement,
    val soundElement: SoundElement,
    val soundBalance: SoundBalance,
    val strokeBalance: StrokeBalance,
    val bornComplElement: BornComplElement,
    val bornElementCount: String
) {
    companion object {
        fun build(nameEvaluationResult: NameEvaluationResult): NamingReport {
            var totalOhaengString = ""
            nameEvaluationResult.seongmyeonghak?.sajuNameOhaeng?.ohaengDistribution?.forEach {
                totalOhaengString += it.key + ":" + it.value + ", "
            }

            return with (nameEvaluationResult) {
                NamingReport(
                    fullName,
                    fullNameHanja,
                    totalScore,
                    nameEvaluationResult.overallSummary?.overallSummary ?: "",
                    statistics?.gender?.let {
                        Statistics(
                            description = it.characteristic,
                            details = it.interpretation?.recommendations ?: "",
                            maleRatio = it.malePercentage,
                            rawData = statistics?.rawData,
                            score = it.score
                        )
                    } ?: Statistics.EMPTY,

                    seongmyeonghak?.sageokSuri?.let {
                        PatternStroke(
                            description = it.score?.reason ?: "",
                            details = it.interpretation?.let { detail -> detail.overallFortune + "\n\n" + detail.lifePathGuidance } ?: "",
                            score = it.score?.score ?: 0
                        )
                    } ?: PatternStroke.EMPTY,

                    seongmyeonghak?.sageokSuriOhaeng?.let {
                        PatternStrokeElement(
                            description = it.score?.reason ?: "",
                            details = it.interpretation?.recommendations ?: "",
                            score = it.score?.score ?: 0
                        )
                    } ?: PatternStrokeElement.EMPTY,

                    seongmyeonghak?.baleumOhaeng?.let {
                        SoundElement(
                            description = it.score?.reason ?: "",
                            details = it.interpretation?.recommendations ?: "",
                            score = it.score?.score ?: 0
                        )
                    } ?: SoundElement.EMPTY,

                    seongmyeonghak?.baleumEumYang?.let {
                        SoundBalance(
                            description = it.score?.reason ?: "",
                            details = it.interpretation?.recommendations ?: "",
                            score = it.score?.score ?: 0
                        )
                    } ?: SoundBalance.EMPTY,

                    seongmyeonghak?.sageokSuriEumYang?.let {
                        StrokeBalance(
                            description = it.score?.reason ?: "",
                            details = it.interpretation?.recommendations ?: "",
                            score = it.score?.score ?: 0
                        )
                    } ?: StrokeBalance.EMPTY,

                    seongmyeonghak?.sajuNameOhaeng?.let {
                        BornComplElement(
                            description = it.score?.reason ?: "",
                            details = it.interpretation?.recommendations ?: "",
                            score = it.score?.score ?: 0
                        )
                    } ?: BornComplElement.EMPTY,
                    totalOhaengString
                )
            }
        }
    }
    val reportItems = arrayListOf(
        ReportItem.from(statistics),
        ReportItem.from(patternStroke),
        ReportItem.from(patternStrokeElement),
        ReportItem.from(soundElement),
        ReportItem.from(soundBalance),
        ReportItem.from(strokeBalance),
        ReportItem.from(bornComplElement)
    )
}