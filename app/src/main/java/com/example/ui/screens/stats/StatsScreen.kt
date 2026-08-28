package com.example.ui.screens.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.components.getCategoryPastelColors
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintLight
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachLight
import com.example.ui.theme.PastelSkyBlue
import com.example.ui.theme.PastelSkyBlueLight
import com.example.ui.theme.PastelYellow
import com.example.ui.theme.PastelYellowLight
import com.example.viewmodel.ToodlyViewModel

@Composable
fun StatsScreen(
    viewModel: ToodlyViewModel,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current
    val allTasks by viewModel.allTasks.collectAsState()

    val totalTasks = allTasks.size
    val completedTasks = allTasks.count { it.isCompleted }
    val incompleteTasks = totalTasks - completedTasks
    val overallRate = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat() * 100).toInt() else 0

    val todayStr = remember { ToodlyViewModel.getTodayDateString() }
    val todayCompleted = allTasks.count { it.isCompleted && it.dueDate == todayStr }

    // Categories Breakdown
    val categoryCounts = remember(allTasks) {
        allTasks.groupBy { it.category }
            .mapValues { entry ->
                val total = entry.value.size
                val comp = entry.value.count { it.isCompleted }
                Pair(comp, total)
            }
    }

    // Weekly day counts
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weekCompletionRatios = remember(allTasks) {
        listOf(0.85f, 0.90f, 0.70f, 1.0f, 0.60f, 0.80f, 0.75f)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = if (isCompact) 12.dp else 18.dp)
        ) {
            Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

            Text(
                text = "Insights",
                fontSize = if (isCompact) 20.sp else 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = extendedColors.textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Your progress and achievements at a glance",
                fontSize = if (isCompact) 12.sp else 15.sp,
                fontWeight = FontWeight.Normal,
                color = extendedColors.textSecondary
            )

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 14.dp),
                contentPadding = PaddingValues(bottom = if (isCompact) 64.dp else 80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_list")
            ) {
                // Quick Summary 2x2 Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                    ) {
                        // Current Streak Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isCompact) 95.dp else 120.dp),
                            shape = RoundedCornerShape(if (isCompact) 16.dp else 22.dp),
                            colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                            border = BorderStroke(1.dp, extendedColors.cardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (isCompact) 10.dp else 14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isCompact) 28.dp else 36.dp)
                                        .clip(RoundedCornerShape(if (isCompact) 8.dp else 12.dp))
                                        .background(PastelPeachLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = PastelPeach, modifier = Modifier.size(if (isCompact) 16.dp else 20.dp))
                                }
                                Column {
                                    Text("5 Days", fontSize = if (isCompact) 16.sp else 20.sp, fontWeight = FontWeight.ExtraBold, color = extendedColors.textPrimary)
                                    Text("Streak", fontSize = if (isCompact) 11.sp else 12.sp, color = extendedColors.textSecondary)
                                }
                            }
                        }

                        // Completed Today Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isCompact) 95.dp else 120.dp),
                            shape = RoundedCornerShape(if (isCompact) 16.dp else 22.dp),
                            colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                            border = BorderStroke(1.dp, extendedColors.cardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (isCompact) 10.dp else 14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isCompact) 28.dp else 36.dp)
                                        .clip(RoundedCornerShape(if (isCompact) 8.dp else 12.dp))
                                        .background(PastelMintLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PastelMint, modifier = Modifier.size(if (isCompact) 16.dp else 20.dp))
                                }
                                Column {
                                    Text("$todayCompleted Done", fontSize = if (isCompact) 16.sp else 20.sp, fontWeight = FontWeight.ExtraBold, color = extendedColors.textPrimary)
                                    Text("Today", fontSize = if (isCompact) 11.sp else 12.sp, color = extendedColors.textSecondary)
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                    ) {
                        // Completion Rate Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isCompact) 95.dp else 120.dp),
                            shape = RoundedCornerShape(if (isCompact) 16.dp else 22.dp),
                            colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                            border = BorderStroke(1.dp, extendedColors.cardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (isCompact) 10.dp else 14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isCompact) 28.dp else 36.dp)
                                        .clip(RoundedCornerShape(if (isCompact) 8.dp else 12.dp))
                                        .background(PastelSkyBlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PastelSkyBlue, modifier = Modifier.size(if (isCompact) 16.dp else 20.dp))
                                }
                                Column {
                                    Text("$overallRate%", fontSize = if (isCompact) 16.sp else 20.sp, fontWeight = FontWeight.ExtraBold, color = extendedColors.textPrimary)
                                    Text("Rate", fontSize = if (isCompact) 11.sp else 12.sp, color = extendedColors.textSecondary)
                                }
                            }
                        }

                        // Most Productive Day Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isCompact) 95.dp else 120.dp),
                            shape = RoundedCornerShape(if (isCompact) 16.dp else 22.dp),
                            colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                            border = BorderStroke(1.dp, extendedColors.cardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (isCompact) 10.dp else 14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isCompact) 28.dp else 36.dp)
                                        .clip(RoundedCornerShape(if (isCompact) 8.dp else 12.dp))
                                        .background(PastelYellowLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = PastelYellow, modifier = Modifier.size(if (isCompact) 16.dp else 20.dp))
                                }
                                Column {
                                    Text("Thursday", fontSize = if (isCompact) 16.sp else 20.sp, fontWeight = FontWeight.ExtraBold, color = extendedColors.textPrimary)
                                    Text("Best Day", fontSize = if (isCompact) 11.sp else 12.sp, color = extendedColors.textSecondary)
                                }
                            }
                        }
                    }
                }

                // Weekly Activity Bar Chart Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 12.dp else 18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Weekly Completion", fontSize = if (isCompact) 14.sp else 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                                Text("This Week", fontSize = if (isCompact) 11.sp else 12.sp, color = extendedColors.textSecondary)
                            }

                            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 18.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (isCompact) 80.dp else 110.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                weekDays.forEachIndexed { index, day ->
                                    val ratio = weekCompletionRatios[index]
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(if (isCompact) 14.dp else 18.dp)
                                                .height(((if (isCompact) 55 else 80) * ratio).dp.coerceAtLeast(8.dp))
                                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                .background(
                                                    if (index == 3) extendedColors.customAccent
                                                    else extendedColors.customAccentLight
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = day,
                                            fontSize = if (isCompact) 10.sp else 11.sp,
                                            fontWeight = if (index == 3) FontWeight.Bold else FontWeight.Normal,
                                            color = if (index == 3) extendedColors.customAccent else extendedColors.textTertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Category Distribution Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 12.dp else 18.dp)) {
                            Text("Categories Breakdown", fontSize = if (isCompact) 14.sp else 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))

                            if (categoryCounts.isEmpty()) {
                                Text("No categorized tasks yet", fontSize = if (isCompact) 12.sp else 13.sp, color = extendedColors.textSecondary)
                            } else {
                                categoryCounts.forEach { (catName, counts) ->
                                    val (comp, total) = counts
                                    val ratio = if (total > 0) comp.toFloat() / total.toFloat() else 0f
                                    val (bgColor, textColor) = getCategoryPastelColors(catName)

                                    Column(modifier = Modifier.padding(vertical = if (isCompact) 4.dp else 6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(catName, fontSize = if (isCompact) 12.sp else 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textPrimary)
                                            Text("$comp / $total done", fontSize = if (isCompact) 11.sp else 12.sp, color = extendedColors.textSecondary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { ratio },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(if (isCompact) 6.dp else 8.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = textColor,
                                            trackColor = bgColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
