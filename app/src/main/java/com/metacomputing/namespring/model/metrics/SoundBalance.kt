package com.metacomputing.namespring.model.metrics

class SoundBalance(
    topic: String = "발음음양",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score)