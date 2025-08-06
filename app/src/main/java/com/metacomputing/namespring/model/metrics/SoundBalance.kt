package com.metacomputing.namespring.model.metrics

// 발음음양
class SoundBalance(
    topic: String = "발음음양",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score) {
    companion object {
        val EMPTY = SoundBalance(description = "", details = "", score = 0)
    }
}