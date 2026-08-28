package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors

data class DayOverview(
    val dayAbbreviation: String, // "Mon", "Tue", ...
    val dayLetter: String,       // "M", "T", ...
    val dateString: String,      // "2026-08-28"
    val dayOfMonth: Int,         // 28
    val completedCount: Int,
    val totalCount: Int,
    val isToday: Boolean
) {
    val completionRate: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
}

@Composable
fun WeeklyOverview(
    completedTasks: Int,
    totalTasks: Int,
    weekDays: List<DayOverview> = emptyList(),
    weekRangeLabel: String = "This Week",
    modifier: Modifier = Modifier,
    onDayClick: ((DayOverview) -> Unit)? = null
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current

    val rawProgress = if (totalTasks > 0) {
        (completedTasks.toFloat() / totalTasks.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 600),
        label = "weekly_progress_anim"
    )

    val percentage = (rawProgress * 100).toInt()

    val statusText = when {
        totalTasks == 0 -> "No tasks this week"
        percentage == 100 -> "All tasks completed! 🎉"
        percentage >= 75 -> "Great momentum! 🌟"
        percentage >= 50 -> "Halfway there! 🎯"
        percentage > 0 -> "In progress ✨"
        else -> "Ready to start 🚀"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_overview_card"),
        shape = RoundedCornerShape(if (isCompact) 16.dp else 22.dp),
        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
        border = BorderStroke(1.dp, extendedColors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 12.dp else 16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 30.dp else 36.dp)
                            .clip(RoundedCornerShape(if (isCompact) 8.dp else 10.dp))
                            .background(extendedColors.customAccentLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = extendedColors.customAccent,
                            modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Weekly Overview",
                            fontSize = if (isCompact) 14.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.textPrimary
                        )
                        Text(
                            text = weekRangeLabel,
                            fontSize = if (isCompact) 11.sp else 12.sp,
                            color = extendedColors.textSecondary
                        )
                    }
                }

                // Rate Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(extendedColors.customAccentLight)
                        .padding(horizontal = if (isCompact) 8.dp else 10.dp, vertical = 4.dp)
                        .testTag("weekly_completion_badge")
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = if (isCompact) 12.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.customAccent,
                        modifier = Modifier.testTag("weekly_completion_rate_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))

            // Percentage & Counter Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = if (totalTasks > 0) "$completedTasks of $totalTasks completed" else "0 tasks scheduled",
                        fontSize = if (isCompact) 12.sp else 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = extendedColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = statusText,
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        color = extendedColors.textSecondary
                    )
                }

                if (totalTasks > 0) {
                    val remaining = (totalTasks - completedTasks).coerceAtLeast(0)
                    Text(
                        text = if (remaining == 0) "100% Done" else "$remaining left",
                        fontSize = if (isCompact) 11.sp else 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (remaining == 0) extendedColors.customAccent else extendedColors.textTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 10.dp))

            // Simple Progress Indicator
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 7.dp else 9.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .testTag("weekly_progress_indicator"),
                color = extendedColors.customAccent,
                trackColor = extendedColors.subtleBackground
            )

            // Day-by-Day Mini Strip (if weekDays provided)
            if (weekDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDays.forEach { day ->
                        val isDayComplete = day.totalCount > 0 && day.completedCount == day.totalCount
                        val hasTasks = day.totalCount > 0

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = onDayClick != null) { onDayClick?.invoke(day) }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = day.dayLetter,
                                fontSize = if (isCompact) 10.5.sp else 11.5.sp,
                                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                                color = if (day.isToday) extendedColors.customAccent else extendedColors.textSecondary
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Box(
                                modifier = Modifier
                                    .size(if (isCompact) 22.dp else 26.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            day.isToday -> extendedColors.customAccent
                                            isDayComplete -> extendedColors.customAccentLight
                                            hasTasks -> extendedColors.subtleBackground
                                            else -> Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDayComplete && !day.isToday) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = extendedColors.customAccent,
                                        modifier = Modifier.size(if (isCompact) 12.dp else 14.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${day.dayOfMonth}",
                                        fontSize = if (isCompact) 10.sp else 11.sp,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (day.isToday) Color.White else extendedColors.textPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Small indicator dot for task activity
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDayComplete -> extendedColors.customAccent
                                            hasTasks -> extendedColors.customAccentLight
                                            else -> Color.Transparent
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
