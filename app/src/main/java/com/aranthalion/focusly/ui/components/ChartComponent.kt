package com.aranthalion.focusly.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.aranthalion.focusly.data.model.ChartData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun ChartComponent(
    title: String,
    chartData: ChartData,
    chartType: ChartType = ChartType.BAR,
    modifier: Modifier = Modifier,
    showValues: Boolean = true,
    color: Int = Color.parseColor("#2196F3")
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (chartData.values.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay datos disponibles",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                when (chartType) {
                    ChartType.BAR -> BarChartComponent(chartData = chartData, showValues = showValues, color = color)
                    ChartType.LINE -> LineChartComponent(chartData = chartData, showValues = showValues, color = color)
                }
            }
        }
    }
}

@Composable
fun BarChartComponent(
    chartData: ChartData,
    showValues: Boolean = true,
    color: Int = Color.parseColor("#2196F3")
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index < chartData.labels.size) {
                                chartData.labels[index]
                            } else ""
                        }
                    }
                }
                
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }
                
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = chartData.values.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value)
            }
            
            val dataSet = BarDataSet(entries, "Datos").apply {
                this.color = color
                setDrawValues(showValues)
            }
            
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun LineChartComponent(
    chartData: ChartData,
    showValues: Boolean = true,
    color: Int = Color.parseColor("#4CAF50")
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index < chartData.labels.size) {
                                chartData.labels[index]
                            } else ""
                        }
                    }
                }
                
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }
                
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = chartData.values.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }
            
            val dataSet = LineDataSet(entries, "Datos").apply {
                this.color = color
                setDrawCircles(true)
                setDrawValues(showValues)
                lineWidth = 2f
            }
            
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

enum class ChartType {
    BAR,
    LINE
} 