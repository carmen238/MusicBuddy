package com.example.musicbuddy.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicbuddy.ui.theme.AppColors

// ============= DATA CLASSES =============

data class BarChartData(
    val label: String,
    val value: Float,
    val color: Color
)

// ============= REFINED BAR CHART =============

/**
 * Horizontal Bar Chart - Modern and refined version
 */
@Composable
fun HorizontalBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    title: String = "Distribuzione"
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        // Title
        Text(
            title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.DarkText,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Bars
        data.forEach { item ->
            RefinedBarItem(
                label = item.label,
                value = item.value,
                maxValue = maxValue,
                color = item.color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }
    }
}

/**
 * Single refined bar item with animation
 */
@Composable
private fun RefinedBarItem(
    label: String,
    value: Float,
    maxValue: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val percentage = (value / maxValue * 100).toInt()
    var animationPlayed by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) (value / maxValue) else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "barAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(modifier = modifier) {
        // Label and percentage
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontSize = 13.sp,
                color = AppColors.DarkText,
                fontWeight = FontWeight.Medium
            )
            Text(
                "$percentage%",
                fontSize = 12.sp,
                color = AppColors.LightText,
                fontWeight = FontWeight.Medium
            )
        }

        // Bar background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(
                    color = Color(0xFFE8E8E8),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            // Bar fill with animation
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

// ============= STATISTICS CARD =============

/**
 * Statistics card with multiple metrics
 */
@Composable
fun StatisticsCard(
    title: String,
    stats: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        // Title
        Text(
            title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.DarkText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Stats
        stats.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    fontSize = 13.sp,
                    color = AppColors.LightText
                )
                Text(
                    value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.DarkText
                )
            }

            // Divider (except last item)
            if (index < stats.size - 1) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE8E8E8))
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

// ============= PROGRESS INDICATOR =============

/**
 * Refined circular progress indicator
 */
@Composable
fun CircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = AppColors.PrimaryGreen,
    size: Float = 100f
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${(progress * 100).toInt()}%",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.DarkText
        )
    }
}

// ============= METRIC CARD =============

/**
 * Simple metric card for displaying single values
 */
@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = AppColors.LightText,
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.DarkText,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}