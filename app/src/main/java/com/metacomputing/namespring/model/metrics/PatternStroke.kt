package com.metacomputing.namespring.model.metrics

// 사격수리
class PatternStroke(
    topic: String = "사격수리",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score) {
    companion object {
        val EMPTY = PatternStroke(description = "", details = "", score = 0)
    }
}