package com.example.ui.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.ui.components.QuickAddSheet
import com.example.ui.components.TaskCard
import com.example.ui.screens.taskdetail.TaskDetailDialog
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.viewmodel.CalendarViewMode
import com.example.viewmodel.ToodlyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: ToodlyViewModel,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current
    val allTasks by viewModel.allTasks.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val selectedDateStr by viewModel.selectedCalendarDate.collectAsState()
    val viewMode by viewModel.calendarViewMode.collectAsState()

    var currentCalendarMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var showQuickAdd by remember { mutableStateOf(false) }
    var selectedTaskForDetail by remember { mutableStateOf<TaskItem?>(null) }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayTasks = remember(allTasks, selectedDateStr) {
        allTasks.filter { it.dueDate == selectedDateStr }
    }

    // Days in current month
    val daysInMonth = remember(currentCalendarMonth) {
        val cal = currentCalendarMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val list = mutableListOf<String?>()

        // padding before 1st day (assuming Monday start)
        val offset = (firstDayOfWeek + 5) % 7 // Monday = 0, Sunday = 6
        for (i in 0 until offset) {
            list.add(null)
        }
        for (day in 1..maxDays) {
            val dateFormatted = String.format(
                "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                day
            )
            list.add(dateFormatted)
        }
        list
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickAdd = true },
                containerColor = extendedColors.customAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("calendar_fab_add_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = if (isCompact) 12.dp else 18.dp)
        ) {
            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

            // Header & View Mode Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calendar",
                    fontSize = if (isCompact) 20.sp else 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = extendedColors.textPrimary
                )

                // Month / Week switch
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(extendedColors.subtleBackground)
                        .padding(2.dp)
                ) {
                    listOf(CalendarViewMode.MONTH, CalendarViewMode.WEEK).forEach { mode ->
                        val isSelected = viewMode == mode
                        val label = if (mode == CalendarViewMode.MONTH) "Month" else "Week"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) extendedColors.customAccent else Color.Transparent)
                                .clickable { viewModel.setCalendarViewMode(mode) }
                                .padding(horizontal = if (isCompact) 9.dp else 12.dp, vertical = if (isCompact) 4.dp else 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = if (isCompact) 11.sp else 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else extendedColors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))

            // Month Navigation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                border = BorderStroke(1.dp, extendedColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(if (isCompact) 10.dp else 16.dp)) {
                    // Month title & arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = monthYearFormat.format(currentCalendarMonth.time),
                            fontSize = if (isCompact) 14.5.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.textPrimary
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    val nextCal = currentCalendarMonth.clone() as Calendar
                                    nextCal.add(Calendar.MONTH, -1)
                                    currentCalendarMonth = nextCal
                                },
                                modifier = Modifier.size(if (isCompact) 32.dp else 40.dp)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = extendedColors.textPrimary, modifier = Modifier.size(if (isCompact) 18.dp else 24.dp))
                            }
                            IconButton(
                                onClick = {
                                    val nextCal = currentCalendarMonth.clone() as Calendar
                                    nextCal.add(Calendar.MONTH, 1)
                                    currentCalendarMonth = nextCal
                                },
                                modifier = Modifier.size(if (isCompact) 32.dp else 40.dp)
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = extendedColors.textPrimary, modifier = Modifier.size(if (isCompact) 18.dp else 24.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                    // Day of week headers
                    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekDays.forEach { d ->
                            Text(
                                text = d,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = if (isCompact) 10.sp else 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = extendedColors.textTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                    // Dates Grid
                    val gridItems = if (viewMode == CalendarViewMode.MONTH) {
                        daysInMonth
                    } else {
                        // Week view: show 7 days around selected date
                        daysInMonth.filterNotNull().take(7)
                    }

                    val gridHeight = if (viewMode == CalendarViewMode.MONTH) (if (isCompact) 180.dp else 220.dp) else (if (isCompact) 42.dp else 50.dp)
                    val dateCellSize = if (isCompact) 27.dp else 34.dp

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(gridHeight),
                        userScrollEnabled = false
                    ) {
                        items(gridItems) { dateStr ->
                            if (dateStr == null) {
                                Box(modifier = Modifier.size(dateCellSize))
                            } else {
                                val dayNum = dateStr.split("-").last().toInt()
                                val isSelected = dateStr == selectedDateStr
                                val isToday = dateStr == ToodlyViewModel.getTodayDateString()
                                val taskCount = allTasks.count { it.dueDate == dateStr && !it.isCompleted }

                                Box(
                                    modifier = Modifier
                                        .padding(1.5.dp)
                                        .size(dateCellSize)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) extendedColors.customAccent
                                             else if (isToday) extendedColors.customAccentLight
                                            else Color.Transparent
                                        )
                                        .clickable { viewModel.setSelectedCalendarDate(dateStr) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            fontSize = if (isCompact) 11.5.sp else 13.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else if (isToday) extendedColors.customAccent else extendedColors.textPrimary
                                        )
                                        if (taskCount > 0 && !isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(3.dp)
                                                    .clip(CircleShape)
                                                    .background(extendedColors.customAccent)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 16.dp))

            // Tasks for selected date header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tasks for ${formatFriendlyDate(selectedDateStr)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.textPrimary
                )

                Text(
                    text = "${dayTasks.size} tasks",
                    fontSize = 13.sp,
                    color = extendedColors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 10.dp))

            // Tasks List
            if (dayTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tasks for this date",
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = extendedColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { showQuickAdd = true }) {
                            Text("+ Add task for this day", color = extendedColors.customAccent, fontWeight = FontWeight.Bold, fontSize = if (isCompact) 13.sp else 14.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp),
                    contentPadding = PaddingValues(bottom = if (isCompact) 64.dp else 80.dp)
                ) {
                    items(
                        items = dayTasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(it) },
                            onClick = { selectedTaskForDetail = task },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    if (showQuickAdd) {
        QuickAddSheet(
            categories = categories,
            onDismiss = { showQuickAdd = false },
            onAddTask = { title, category, _, dueTime, priority ->
                viewModel.quickAddTask(title, category, selectedDateStr, dueTime, priority)
            }
        )
    }

    if (selectedTaskForDetail != null) {
        TaskDetailDialog(
            task = selectedTaskForDetail!!,
            categories = categories,
            onDismiss = { selectedTaskForDetail = null },
            onSave = { updated -> viewModel.saveTask(updated) },
            onDuplicate = { viewModel.duplicateTask(it) },
            onDelete = { viewModel.deleteTask(it) }
        )
    }
}

fun formatFriendlyDate(dateStr: String): String {
    val today = ToodlyViewModel.getTodayDateString()
    val tomorrow = ToodlyViewModel.getOffsetDateString(1)
    return when (dateStr) {
        today -> "Today"
        tomorrow -> "Tomorrow"
        else -> {
            try {
                val parts = dateStr.split("-")
                val months = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                "${months[parts[1].toInt()]} ${parts[2].toInt()}"
            } catch (_: Exception) {
                dateStr
            }
        }
    }
}
