package org.smartgym.Screens.Adm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.DefaultVerticalBarPosition
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberLinearAxisModel

data class ChartItem(val label: String, val value: Float)

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun RevenueBarChart(
    data: List<ChartItem>,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val labels = data.map { it.label }
    val values = data.map { it.value }
    val maxValue = 80000f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Receita Mensal",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))
            XYGraph(
                xAxisModel = CategoryAxisModel(labels),
                yAxisModel = rememberLinearAxisModel(
                    range = 0f..maxValue,
                    ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                yAxisLabels = { value: Float -> "${(value / 1000).toInt()}k" },
                xAxisLabels = { label: String -> label },
                xAxisTitle = "",
                yAxisTitle = "",
                horizontalMajorGridLineStyle = LineStyle(
                    brush = SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    strokeWidth = 1.dp
                ),
                verticalMajorGridLineStyle = null
            ) {
                VerticalBarPlot(
                    xData = labels,
                    yData = values,
                    bar = { index ->
                        DefaultVerticalBar(
                            color = barColor,
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                    }
                )
            }
        }
    }
}