package com.metacomputing.namespring.model.report

data class ReportItem(
    val title: String,
    val desc: String,
    val score: Int = 0)