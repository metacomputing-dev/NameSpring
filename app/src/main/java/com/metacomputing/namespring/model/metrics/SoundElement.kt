package com.metacomputing.namespring.model.metrics

// 발음오행
class SoundElement(
    topic: String = "발음오행",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score)