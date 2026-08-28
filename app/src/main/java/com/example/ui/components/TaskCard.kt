package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderLight

@Composable
fun TaskCard(
    task: TaskItem,
    onToggleCompletion: (TaskItem) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current
    val isCompleted = task.isCompleted

    val (catBg, catText) = getCategoryPastelColors(task.category)
    val subtasks = remember(task.subtasksJson) { task.getSubtasks() }
    val completedSubtasks = remember(subtasks) { subtasks.count { it.isCompleted } }

    var showMenu by remember { mutableStateOf(false) }

    val cornerRadius = if (isCompact) 14.dp else 20.dp
    val cardPaddingHorizontal = if (isCompact) 12.dp else 16.dp
    val cardPaddingVertical = if (isCompact) 9.dp else 14.dp
    val titleFontSize = if (isCompact) 14.5.sp else 16.sp
    val subtitleSpacing = if (isCompact) 3.dp else 6.dp
    val checkboxTouchSize = if (isCompact) 38.dp else 48.dp
    val checkboxCircleSize = if (isCompact) 22.dp else 26.dp
    val actionButtonSize = if (isCompact) 30.dp else 36.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = extendedColors.cardBackground
        ),
        border = BorderStroke(1.dp, extendedColors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = cardPaddingHorizontal, vertical = cardPaddingVertical),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Custom Round Checkbox
            val scale by animateFloatAsState(
                targetValue = if (isCompleted) 1.08f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "checkScale"
            )

            val checkboxBg by animateColorAsState(
                targetValue = if (isCompleted) extendedColors.customAccent else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "checkBg"
            )

            val checkboxBorder by animateColorAsState(
                targetValue = if (isCompleted) extendedColors.customAccent else extendedColors.cardBorder.copy(alpha = 0.9f),
                label = "checkBorder"
            )

            Box(
                modifier = Modifier
                    .size(checkboxTouchSize)
                    .clickable { onToggleCompletion(task) }
                    .testTag("checkbox_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(checkboxCircleSize)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(checkboxBg)
                        .border(1.6.dp, checkboxBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(if (isCompact) 13.dp else 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 10.dp))

            // Task Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = task.title,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) extendedColors.textTertiary else extendedColors.textPrimary,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle info pills
                Spacer(modifier = Modifier.height(subtitleSpacing))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val pillHorizPadding = if (isCompact) 6.dp else 8.dp
                    val pillVertPadding = if (isCompact) 1.5.dp else 2.dp

                    if (task.dueTime.isNotBlank()) {
                        PastelPill(
                            text = task.dueTime,
                            backgroundColor = extendedColors.customAccentLight,
                            textColor = extendedColors.customAccent,
                            horizontalPadding = pillHorizPadding,
                            verticalPadding = pillVertPadding
                        )
                    } else if (task.dueDate.isNotBlank()) {
                        PastelPill(
                            text = formatShortDate(task.dueDate),
                            backgroundColor = extendedColors.customAccentLight,
                            textColor = extendedColors.customAccent,
                            horizontalPadding = pillHorizPadding,
                            verticalPadding = pillVertPadding
                        )
                    }

                    if (task.category.isNotBlank()) {
                        PastelPill(
                            text = task.category,
                            backgroundColor = catBg,
                            textColor = catText,
                            horizontalPadding = pillHorizPadding,
                            verticalPadding = pillVertPadding
                        )
                    }

                    if (subtasks.isNotEmpty()) {
                        PastelPill(
                            text = "$completedSubtasks/${subtasks.size}",
                            icon = Icons.Default.CheckCircleOutline,
                            backgroundColor = extendedColors.subtleBackground,
                            textColor = extendedColors.textSecondary,
                            horizontalPadding = if (isCompact) 5.dp else 6.dp,
                            verticalPadding = pillVertPadding
                        )
                    }

                    if (task.priority.uppercase() != "MEDIUM") {
                        PriorityBadge(priority = task.priority)
                    }
                }
            }

            // Right Action / Chevron
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier
                    .size(actionButtonSize)
                    .testTag("task_menu_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Details",
                    tint = extendedColors.textTertiary,
                    modifier = Modifier.size(if (isCompact) 18.dp else 24.dp)
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(extendedColors.cardBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Details", color = extendedColors.textPrimary) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = extendedColors.customAccent) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Task", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

fun formatShortDate(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        if (parts.size == 3) {
            val monthInt = parts[1].toInt()
            val dayInt = parts[2].toInt()
            val months = listOf("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            "${months[monthInt]} $dayInt"
        } else dateStr
    } catch (_: Exception) {
        dateStr
    }
}
