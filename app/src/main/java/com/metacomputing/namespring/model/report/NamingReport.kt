package com.metacomputing.namespring.model.report

import com.metacomputing.namespring.model.metrics.BornComplElement
import com.metacomputing.namespring.model.metrics.PatternStroke
import com.metacomputing.namespring.model.metrics.PatternStrokeElement
import com.metacomputing.namespring.model.metrics.SoundBalance
import com.metacomputing.namespring.model.metrics.SoundElement
import com.metacomputing.namespring.model.metrics.StrokeBalance
import com.metacomputing.seed.model.NameEvaluationResult

data class NamingReport(
    val name: String,
    val hanja: String,
    val totalScore: Int,
    val overallReview: String,
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
            return with (nameEvaluationResult) {
                NamingReport(
                    fullName,
                    fullNameHanja,
                    totalScore,
                    "TODO\n 이름에 대한 종합 총평\n 생성 필요",
                    PatternStroke(
                        description = details.detailedScores.sageokSuriScore.reason,
                        details = interpretation?.sageokSuriInterpretation?.let { it.overallFortune + "\n\n" + it.lifePathGuidance } ?: "",
                        score = details.detailedScores.sageokSuriScore.score
                    ),
                    PatternStrokeElement(
                        description = details.detailedScores.sageokSuriOhaengScore.reason,
                        details = interpretation?.sageokSuriOhaengInterpretation?.recommendations ?: "",
                        score = details.detailedScores.sageokSuriOhaengScore.score
                    ),
                    SoundElement(
                        description = details.detailedScores.baleumOhaengScore.reason,
                        details = interpretation?.baleumOhaengInterpretation?.recommendations ?: "",
                        score = details.detailedScores.baleumOhaengScore.score
                    ),
                    SoundBalance(
                        description = details.detailedScores.baleumEumYangScore.reason,
                        details = interpretation?.baleumEumYangInterpretation?.recommendations ?: "",
                        score = details.detailedScores.baleumEumYangScore.score
                    ),
                    StrokeBalance(
                        description = details.detailedScores.hoeksuEumYangScore.reason,
                        details = interpretation?.hoeksuEumYangInterpretation?.recommendations ?: "",
                        score = details.detailedScores.hoeksuEumYangScore.score
                    ),
                    BornComplElement(
                        description = details.detailedScores.sajuNameOhaengScore.reason,
                        details = interpretation?.sajuOhaengInterpretation?.recommendations ?: "",
                        score = details.detailedScores.sajuNameOhaengScore.score
                    ),
                    "interpretation?.sajuOhaengInterpretation?.ohaengDistribution"
                )
            }
        }
    }
    val reportItems = arrayListOf(
        ReportItem.from(patternStroke),
        ReportItem.from(patternStrokeElement),
        ReportItem.from(soundElement),
        ReportItem.from(soundBalance),
        ReportItem.from(strokeBalance),
        ReportItem.from(bornComplElement)
    )
}