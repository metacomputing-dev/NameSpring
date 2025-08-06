package com.metacomputing.namespring.model.report

import com.metacomputing.namespring.model.metrics.NameMetrics

data class ReportItem(
    val title: String,
    val desc: String,
    val details: String,
    val score: Int = 0) {
    val scoreString: String = scoreToString()

    companion object {
        fun from(metric: NameMetrics): ReportItem {
            return with (metric) { ReportItem(topic, toMultiLine(description), details, score) }
        }

        private fun toMultiLine(string: String): String { // TODO move it to seed engine
            return string.replace(", ", "\n")
        }
    }

    private fun scoreToString(): String {
        val rank = if (score >= 75) {
            "상"
        } else if (score >= 60) {
            "중"
        } else {
            "하"
        }
        return "$rank ($score 점)"
    }
}