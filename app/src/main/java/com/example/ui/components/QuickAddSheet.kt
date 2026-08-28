package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.viewmodel.ToodlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAddTask: (title: String, category: String, dueDate: String, dueTime: String, priority: String, hasReminder: Boolean) -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first().name else "Personal") }
    var selectedDueDate by remember { mutableStateOf(ToodlyViewModel.getTodayDateString()) }
    var selectedDueTime by remember { mutableStateOf("9:00 AM") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }
    var hasReminder by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = extendedColors.cardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = if (isCompact) 6.dp else 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(extendedColors.cardBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = if (isCompact) 16.dp else 20.dp, vertical = if (isCompact) 4.dp else 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Task",
                    fontSize = if (isCompact) 18.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.textPrimary
                )

                FilterChip(
                    selected = hasReminder,
                    onClick = { hasReminder = !hasReminder },
                    label = { Text(if (hasReminder) "Reminder On 🔔" else "No Reminder", fontSize = 12.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = extendedColors.customAccentLight,
                        selectedLabelColor = extendedColors.customAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))

            // Task input field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        "e.g. Buy groceries, Read 20 mins...",
                        color = extendedColors.textTertiary,
                        fontSize = if (isCompact) 14.sp else 16.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .testTag("quick_add_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = extendedColors.customAccent,
                    unfocusedBorderColor = extendedColors.cardBorder,
                    focusedContainerColor = extendedColors.subtleBackground,
                    unfocusedContainerColor = extendedColors.subtleBackground
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (title.isNotBlank()) {
                            onAddTask(title, selectedCategory, selectedDueDate, selectedDueTime, selectedPriority, hasReminder)
                            onDismiss()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))

            // Date & Time quick chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val todayStr = ToodlyViewModel.getTodayDateString()
                val tomorrowStr = ToodlyViewModel.getOffsetDateString(1)

                FilterChip(
                    selected = selectedDueDate == todayStr,
                    onClick = { selectedDueDate = todayStr },
                    label = { Text("Today", fontSize = if (isCompact) 12.sp else 13.sp) },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(13.dp)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = extendedColors.customAccentLight,
                        selectedLabelColor = extendedColors.customAccent
                    )
                )

                FilterChip(
                    selected = selectedDueDate == tomorrowStr,
                    onClick = { selectedDueDate = tomorrowStr },
                    label = { Text("Tomorrow", fontSize = if (isCompact) 12.sp else 13.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = extendedColors.customAccentLight,
                        selectedLabelColor = extendedColors.customAccent
                    )
                )

                // Time selector chip
                FilterChip(
                    selected = true,
                    onClick = {
                        selectedDueTime = when (selectedDueTime) {
                            "9:00 AM" -> "2:00 PM"
                            "2:00 PM" -> "6:00 PM"
                            "6:00 PM" -> "8:00 PM"
                            else -> "9:00 AM"
                        }
                    },
                    label = { Text(selectedDueTime, fontSize = if (isCompact) 12.sp else 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(13.dp)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = extendedColors.customAccentLight,
                        selectedLabelColor = extendedColors.customAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 12.dp))

            // Category picker row
            Text(
                text = "Category",
                fontSize = if (isCompact) 11.5.sp else 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = extendedColors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))

            val catList = if (categories.isNotEmpty()) categories.map { it.name } else listOf("Personal", "Work", "Study", "Fitness", "Home", "Groceries")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(catList) { cat ->
                    val (bgColor, textColor) = getCategoryPastelColors(cat)
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) textColor else bgColor)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = if (isCompact) 10.dp else 12.dp, vertical = if (isCompact) 4.dp else 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else textColor,
                            fontSize = if (isCompact) 11.5.sp else 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 12.dp))

            // Priority Selector row
            Text(
                text = "Priority",
                fontSize = if (isCompact) 11.5.sp else 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = extendedColors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                    val isSelected = selectedPriority == p
                    val label = when (p) {
                        "LOW" -> "Low"
                        "HIGH" -> "High"
                        else -> "Medium"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                            .clickable { selectedPriority = p }
                            .padding(vertical = if (isCompact) 6.dp else 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else extendedColors.textPrimary,
                            fontSize = if (isCompact) 12.sp else 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 18.dp))

            // Create Task Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddTask(title, selectedCategory, selectedDueDate, selectedDueTime, selectedPriority, hasReminder)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 46.dp else 52.dp)
                    .testTag("quick_add_submit_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = extendedColors.customAccent,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(if (isCompact) 18.dp else 22.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Task", fontSize = if (isCompact) 15.sp else 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))
        }
    }
}
