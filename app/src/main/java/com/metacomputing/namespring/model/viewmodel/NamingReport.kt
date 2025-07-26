package com.metacomputing.namespring.model.viewmodel

data class NamingReport(
    val name: String,
    val totalScore: Int,
    val baleumOhaeng: String,
    val combinedEumyang: String,
    val jawonOhaeng: String,
    val eumYangInfo: String = "TODO",
    val sajuOhaengCount: String
) {
    val reportItems = arrayListOf(
        ReportItem("발음오행", baleumOhaeng),
        ReportItem("발음음양", combinedEumyang),
        ReportItem("수리오행", jawonOhaeng),
        ReportItem("수리음양", eumYangInfo),
        ReportItem("사주오행", sajuOhaengCount)
    )
}