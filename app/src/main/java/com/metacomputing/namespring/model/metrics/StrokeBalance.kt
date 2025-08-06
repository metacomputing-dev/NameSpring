package com.metacomputing.namespring.model.metrics

class StrokeBalance(
    topic: String = "획수음양",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score)