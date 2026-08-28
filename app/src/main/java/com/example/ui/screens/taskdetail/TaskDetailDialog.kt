package com.example.ui.screens.taskdetail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CategoryEntity
import com.example.data.model.Priority
import com.example.data.model.Recurrence
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.ui.components.getCategoryPastelColors
import com.example.ui.theme.LocalExtendedColors
import java.util.Calendar

@Composable
fun TaskDetailDialog(
    task: TaskItem,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (TaskItem) -> Unit,
    onDuplicate: (TaskItem) -> Unit,
    onDelete: (TaskItem) -> Unit
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current

    var title by remember { mutableStateOf(task.title) }
    var notes by remember { mutableStateOf(task.notes) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var dueTime by remember { mutableStateOf(task.dueTime) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var recurrence by remember { mutableStateOf(task.recurrence) }
    var hasReminder by remember { mutableStateOf(task.hasReminder) }
    var reminderTimestamp by remember { mutableStateOf(task.reminderTimestamp) }

    var subtasks by remember { mutableStateOf(task.getSubtasks()) }
    var newSubtaskText by remember { mutableStateOf("") }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .testTag("task_detail_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = extendedColors.textSecondary)
                    }

                    Text(
                        text = "Task Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.textPrimary
                    )

                    Row {
                        IconButton(
                            onClick = {
                                onDuplicate(task)
                                onDismiss()
                            }
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Duplicate", tint = extendedColors.textSecondary)
                        }

                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Divider(color = extendedColors.cardBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Task Title Input
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Task title...", color = extendedColors.textTertiary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("detail_title_input"),
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = extendedColors.textPrimary),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = extendedColors.customAccent,
                                unfocusedBorderColor = extendedColors.cardBorder,
                                focusedContainerColor = extendedColors.subtleBackground,
                                unfocusedContainerColor = extendedColors.subtleBackground
                            )
                        )
                    }

                    // Due Date & Time Pickers
                    item {
                        Text("Date & Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Date Picker Button
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        showDatePicker(context, dueDate) { newDate -> dueDate = newDate }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.subtleBackground)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = extendedColors.customAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (dueDate.isNotBlank()) dueDate else "Set Date",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = extendedColors.textPrimary
                                    )
                                }
                            }

                            // Time Picker Button
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        showTimePicker(context, dueTime) { newTime -> dueTime = newTime }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.subtleBackground)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = extendedColors.customAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (dueTime.isNotBlank()) dueTime else "Set Time",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = extendedColors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Category Selector
                    item {
                        Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        val catList = if (categories.isNotEmpty()) categories.map { it.name } else listOf("Personal", "Work", "Study", "Fitness", "Home", "Groceries")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(catList) { cat ->
                                val (bgColor, textColor) = getCategoryPastelColors(cat)
                                val isSelected = category.equals(cat, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) textColor else bgColor)
                                        .clickable { category = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else textColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Priority Selector
                    item {
                        Text("Priority", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                                val isSelected = priority.equals(p, ignoreCase = true)
                                val label = when (p) {
                                    "LOW" -> "Low"
                                    "HIGH" -> "High"
                                    else -> "Medium"
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                                        .clickable { priority = p }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else extendedColors.textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Recurrence Selector
                    item {
                        Text("Repeat", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("NONE", "DAILY", "WEEKLY", "MONTHLY").forEach { r ->
                                val isSelected = recurrence.equals(r, ignoreCase = true)
                                val label = when (r) {
                                    "DAILY" -> "Daily"
                                    "WEEKLY" -> "Weekly"
                                    "MONTHLY" -> "Monthly"
                                    else -> "None"
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                                        .clickable { recurrence = r }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else extendedColors.textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Reminder Switch
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(extendedColors.subtleBackground)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = extendedColors.customAccent)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Task Reminder Alert", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = extendedColors.textPrimary)
                            }
                            Switch(
                                checked = hasReminder,
                                onCheckedChange = {
                                    hasReminder = it
                                    if (it && reminderTimestamp == null) {
                                        reminderTimestamp = System.currentTimeMillis() + 3600000
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = extendedColors.customAccent
                                )
                            )
                        }
                    }

                    // Subtasks Checklist
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subtasks (${subtasks.count { it.isCompleted }}/${subtasks.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        // Subtask items
                        subtasks.forEachIndexed { index, subtask ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = subtask.isCompleted,
                                    onCheckedChange = { isChecked ->
                                        subtasks = subtasks.toMutableList().also {
                                            it[index] = subtask.copy(isCompleted = isChecked)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = extendedColors.customAccent
                                    )
                                )
                                Text(
                                    text = subtask.title,
                                    fontSize = 14.sp,
                                    color = if (subtask.isCompleted) extendedColors.textTertiary else extendedColors.textPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        subtasks = subtasks.toMutableList().also { it.removeAt(index) }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = extendedColors.textTertiary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Add subtask field
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newSubtaskText,
                                onValueChange = { newSubtaskText = it },
                                placeholder = { Text("Add subtask...", fontSize = 13.sp, color = extendedColors.textTertiary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = extendedColors.customAccent,
                                    unfocusedBorderColor = extendedColors.cardBorder,
                                    focusedContainerColor = extendedColors.subtleBackground,
                                    unfocusedContainerColor = extendedColors.subtleBackground
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (newSubtaskText.isNotBlank()) {
                                        subtasks = subtasks + Subtask(title = newSubtaskText.trim())
                                        newSubtaskText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(extendedColors.customAccent)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add subtask", tint = Color.White)
                            }
                        }
                    }

                    // Notes
                    item {
                        Text("Notes & Details", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Write extra notes, links, or context...", color = extendedColors.textTertiary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = extendedColors.customAccent,
                                unfocusedBorderColor = extendedColors.cardBorder,
                                focusedContainerColor = extendedColors.subtleBackground,
                                unfocusedContainerColor = extendedColors.subtleBackground
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Save Button
                Button(
                    onClick = {
                        val updated = task.copy(
                            title = title.trim(),
                            notes = notes.trim(),
                            dueDate = dueDate,
                            dueTime = dueTime,
                            priority = priority,
                            category = category,
                            recurrence = recurrence,
                            hasReminder = hasReminder,
                            reminderTimestamp = reminderTimestamp,
                            subtasksJson = Subtask.serializeList(subtasks)
                        )
                        onSave(updated)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_task_detail_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.customAccent,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(task)
                        onDismiss()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun showDatePicker(context: Context, currentDateStr: String, onDateSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    if (currentDateStr.isNotBlank()) {
        try {
            val parts = currentDateStr.split("-")
            if (parts.size == 3) {
                cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
        } catch (_: Exception) {}
    }

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(formatted)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun showTimePicker(context: Context, currentTimeStr: String, onTimeSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    var hour = 9
    var minute = 0

    if (currentTimeStr.isNotBlank()) {
        try {
            val parts = currentTimeStr.split(":", " ")
            hour = parts[0].toInt()
            minute = parts[1].toInt()
            val amPm = parts.getOrNull(2) ?: "AM"
            if (amPm.equals("PM", true) && hour < 12) hour += 12
            if (amPm.equals("AM", true) && hour == 12) hour = 0
        } catch (_: Exception) {}
    }

    TimePickerDialog(
        context,
        { _, h, m ->
            val amPm = if (h >= 12) "PM" else "AM"
            val displayHour = if (h == 0) 12 else if (h > 12) h - 12 else h
            val formatted = String.format("%d:%02d %s", displayHour, m, amPm)
            onTimeSelected(formatted)
        },
        hour,
        minute,
        false
    ).show()
}
