package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.databinding.FragmentNamingReportBinding
import com.metacomputing.namespring.databinding.ListItemNamingReportCommonBinding
import com.metacomputing.namespring.model.metrics.Statistics
import com.metacomputing.namespring.model.report.NamingReport
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class NamingReportFragment(
    private val report: NamingReport
): BaseFragment() {
    private lateinit var binding: FragmentNamingReportBinding

    private inner class NamingReportAdapter : RecyclerView.Adapter<NamingReportAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ListItemNamingReportCommonBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListItemNamingReportCommonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount() = report.reportItems.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with (holder.binding) {
                namingReportItemTitle.text = report.reportItems[position].title
                namingReportItemDescription.text = report.reportItems[position].desc
                namingReportItemDetails.text = report.reportItems[position].details
                namingReportItemScore.text = report.reportItems[position].scoreString
                (report.reportItems[position].sourceMetric as? Statistics)?.run {
                    buildPieChart(holder.binding, this)
                    buildLineChart(holder.binding, this)
                }
            }
        }

        private fun buildPieChart(binding: ListItemNamingReportCommonBinding, statistics: Statistics) {
            with (binding) {
                namingReportItemPiechart.visibility = View.VISIBLE
                val entries = listOf(
                    PieEntry(statistics.maleRatio.toFloat(), R.string.gender_male),
                    PieEntry(100 - statistics.maleRatio.toFloat(), R.string.gender_female)
                )
                val dataSet = PieDataSet(entries, resources.getString(R.string.naming_genderRatio)).apply {
                    colors = listOf("#4A90E2".toColorInt(), "#E26A6A".toColorInt())
                    valueTextColor = Color.WHITE
                    valueTextSize = 16f
                    sliceSpace = 3f
                }
                binding.namingReportItemPiechart.apply {
                    data = PieData(dataSet)
                    setDrawEntryLabels(false)

                    setDrawCenterText(true)
                    centerText = "성별 분포"
                    setCenterTextSize(16f)

                    description.isEnabled = false
                    legend.isEnabled = false

                    setUsePercentValues(true)
                    setHoleColor(Color.TRANSPARENT)
                    setTransparentCircleAlpha(0)
                    animateY(600, Easing.EaseInOutQuad)
                    invalidate()
                }
            }
        }

        private fun buildLineChart(binding: ListItemNamingReportCommonBinding, statistics: Statistics) {
            with (binding) {
                namingReportItemLinechart.visibility = View.VISIBLE
                statistics.rawData?.run {
                    val popularity = ArrayList<Pair<Int,Int>>()
                    val entries = ArrayList<Entry>()
                    var startYear = 2008
                    val endYear = Calendar.getInstance().get(Calendar.YEAR)
                    yearlyPopularityRank.male.forEach {
                        val year = it.key.toInt()
                        val rank = it.value
                        popularity.add(Pair(year, rank))
                        if (year < startYear) {
                            startYear = year
                        }
                    }
                    popularity.forEach {
                        entries.add(Entry(it.first.toFloat() - 2000, it.second.toFloat()))
                    }
                    val dataSet = LineDataSet(entries, "년도 별 인기도 추이").apply {
                        color = "#4A90E2".toColorInt()
                        setCircleColor(color)
                        circleRadius = 2f
                        lineWidth = 1f
                        setDrawCircleHole(false)
                        setDrawValues(false)

                        setDrawFilled(true)
                        fillColor = color
                        fillAlpha = 60

                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }

                    namingReportItemLinechart.apply {
                        data = LineData(dataSet)
                        description.isEnabled = false

                        legend.apply {
                            isEnabled = true
                            textColor = Color.DKGRAY
                            textSize = 12f
                            form = Legend.LegendForm.LINE
                        }

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            granularity = 1f
                            axisMinimum = startYear.toFloat() - 2000
                            axisMaximum = endYear.toFloat() - 2000
                            textColor = Color.DKGRAY
                            textSize = 12f
                            x
                            setDrawAxisLine(false)
                            setDrawGridLines(false)
                        }

                        axisLeft.apply {
                            textColor = Color.DKGRAY
                            textSize = 8f
                            setDrawAxisLine(false)
                            setDrawGridLines(true)
                            isInverted = true
                        }
                        axisRight.isEnabled = false

                        setViewPortOffsets(80f, 30f, 40f, 100f) // 여백 조정
                        animateX(600, Easing.EaseInOutCubic)
                        invalidate()
                    }
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentNamingReportBinding.inflate(LayoutInflater.from(requireContext())).apply {
            binding = this
            namingReportRecyclerView.apply {
                layoutManager = LinearLayoutManager(inflater.context)
                adapter = NamingReportAdapter()
            }
            ProfileManager.mainProfile?.clone()?.run {
                namingReportHeaderTextProfileSummary.text =
                    "${report.name}(${report.hanja}) ${gender.value} ($birthAsString)"
            }
            namingReportHeaderTextScore.text = "${report.totalScore}점"

            return root
        }
    }
}