package com.metacomputing.namespring.model.metrics

import com.metacomputing.seed.model.NameStatistics

// 발음음양
class Statistics(
    topic: String = "통계",
    description: String,
    details: String,
    val maleRatio: Double,
    val rawData: NameStatistics?,
    score: Int
) : NameMetrics(topic, description, details, score) {
    companion object {
        val EMPTY = Statistics(description = "", details = "", maleRatio = 0.0, rawData = null, score = 0)
    }
}