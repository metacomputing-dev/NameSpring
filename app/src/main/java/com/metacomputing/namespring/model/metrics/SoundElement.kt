package com.metacomputing.namespring.model.metrics

class SoundElement(
    topic: String = "발음오행",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score)