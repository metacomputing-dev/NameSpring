package com.metacomputing.namespring.model.metrics

// 발음음양
class Statistics(
    topic: String = "통계",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score) {
    companion object {
        val EMPTY = Statistics(description = "", details = "", score = 0)
    }
}