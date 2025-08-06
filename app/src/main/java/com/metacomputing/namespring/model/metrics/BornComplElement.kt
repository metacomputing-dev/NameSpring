package com.metacomputing.namespring.model.metrics

// 사주오행
class BornComplElement(
    topic: String = "사주이름오행",
    description: String,
    details: String,
    score: Int
) : NameMetrics(topic, description, details, score)