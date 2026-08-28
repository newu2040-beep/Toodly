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
import com.example.ui.theme.LocalExtendedColors
import com.example.viewmodel.ToodlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAddTask: (title: String, category: String, dueDate: String, dueTime: String, priority: String) -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first().name else "Personal") }
    var selectedDueDate by remember { mutableStateOf(ToodlyViewModel.getTodayDateString()) }
    var selectedDueTime by remember { mutableStateOf("9:00 AM") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }

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
                    .padding(vertical = 10.dp)
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "New Task",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = extendedColors.textPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Task input field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        "e.g. Buy groceries, Read 20 mins...",
                        color = extendedColors.textTertiary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .testTag("quick_add_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
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
                            onAddTask(title, selectedCategory, selectedDueDate, selectedDueTime, selectedPriority)
                            onDismiss()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Date & Time quick chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val todayStr = ToodlyViewModel.getTodayDateString()
                val tomorrowStr = ToodlyViewModel.getOffsetDateString(1)

                FilterChip(
                    selected = selectedDueDate == todayStr,
                    onClick = { selectedDueDate = todayStr },
                    label = { Text("Today") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = extendedColors.customAccentLight,
                        selectedLabelColor = extendedColors.customAccent
                    )
                )

                FilterChip(
                    selected = selectedDueDate == tomorrowStr,
                    onClick = { selectedDueDate = tomorrowStr },
                    label = { Text("Tomorrow") },
                    shape = RoundedCornerShape(12.dp),
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
                    label = { Text(selectedDueTime) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = extendedColors.customAccentLight,
                        selectedLabelColor = extendedColors.customAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category picker row
            Text(
                text = "Category",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = extendedColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            val catList = if (categories.isNotEmpty()) categories.map { it.name } else listOf("Personal", "Work", "Study", "Fitness", "Home", "Groceries")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(catList) { cat ->
                    val (bgColor, textColor) = getCategoryPastelColors(cat)
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) textColor else bgColor)
                            .clickable { selectedCategory = cat }
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

            Spacer(modifier = Modifier.height(14.dp))

            // Priority Selector row
            Text(
                text = "Priority",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = extendedColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                            .clickable { selectedPriority = p }
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

            Spacer(modifier = Modifier.height(20.dp))

            // Create Task Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddTask(title, selectedCategory, selectedDueDate, selectedDueTime, selectedPriority)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("quick_add_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = extendedColors.customAccent,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Task", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
