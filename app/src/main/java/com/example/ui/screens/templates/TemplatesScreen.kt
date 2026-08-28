package com.example.ui.screens.templates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TemplateEntity
import com.example.ui.components.getCategoryPastelColors
import com.example.ui.components.getIconForName
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.viewmodel.ToodlyViewModel

@Composable
fun TemplatesScreen(
    viewModel: ToodlyViewModel,
    onSelectTemplate: (TemplateEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current
    val templates by viewModel.allTemplates.collectAsState()
    var showCreateCustomTemplateDialog by remember { mutableStateOf(false) }
    var selectedTemplateForDetail by remember { mutableStateOf<TemplateEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateCustomTemplateDialog = true },
                containerColor = extendedColors.customAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("templates_fab_create_custom")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Template", fontWeight = FontWeight.Bold)
                }
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
            Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

            // Header Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 36.dp else 48.dp)
                        .clip(RoundedCornerShape(if (isCompact) 12.dp else 16.dp))
                        .background(extendedColors.customAccentLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = extendedColors.customAccent,
                        modifier = Modifier.size(if (isCompact) 20.dp else 26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Templates",
                        fontSize = if (isCompact) 20.sp else 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = extendedColors.textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Ready-to-use lists for a better you",
                        fontSize = if (isCompact) 12.sp else 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = extendedColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 18.dp))

            // Templates List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp),
                contentPadding = PaddingValues(bottom = if (isCompact) 64.dp else 80.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = templates,
                    key = { it.id }
                ) { template ->
                    val (bgColor, textColor) = getCategoryPastelColors(template.category, template.colorHex)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("template_card_${template.id}")
                            .clip(RoundedCornerShape(if (isCompact) 14.dp else 20.dp))
                            .clickable { selectedTemplateForDetail = template },
                        shape = RoundedCornerShape(if (isCompact) 14.dp else 20.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(if (isCompact) 10.dp else 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Box
                            Box(
                                modifier = Modifier
                                    .size(if (isCompact) 36.dp else 46.dp)
                                    .clip(RoundedCornerShape(if (isCompact) 10.dp else 14.dp))
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getIconForName(template.iconName),
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(if (isCompact) 18.dp else 24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(if (isCompact) 10.dp else 14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = template.name,
                                        fontSize = if (isCompact) 14.sp else 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = extendedColors.textPrimary
                                    )
                                    if (template.isCustom) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(extendedColors.customAccentLight)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Custom", fontSize = 10.sp, color = extendedColors.customAccent, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = template.description,
                                    fontSize = 13.sp,
                                    color = extendedColors.textSecondary,
                                    maxLines = 1
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View",
                                tint = extendedColors.textTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Template Detail Preview Dialog
    if (selectedTemplateForDetail != null) {
        val template = selectedTemplateForDetail!!
        val taskList = remember(template) { template.getTaskList() }
        val (bgColor, textColor) = getCategoryPastelColors(template.category, template.colorHex)

        Dialog(
            onDismissRequest = { selectedTemplateForDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .testTag("template_detail_dialog"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header with back & delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedTemplateForDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = extendedColors.textSecondary)
                        }

                        if (template.isCustom) {
                            IconButton(
                                onClick = {
                                    viewModel.deleteTemplate(template)
                                    selectedTemplateForDetail = null
                                }
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    // Template Title and Icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForName(template.iconName),
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = template.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = template.description,
                            fontSize = 14.sp,
                            color = extendedColors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Estimated time pill
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = extendedColors.subtleBackground),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Timer, contentDescription = null, tint = extendedColors.customAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Estimated Time", fontSize = 13.sp, color = extendedColors.textSecondary)
                                }
                                Text("${template.estimatedTimeMinutes} minutes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Task List", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                        Text("${taskList.size} tasks", fontSize = 13.sp, color = extendedColors.textSecondary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Task Items preview
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(taskList) { taskTitle ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.subtleBackground),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(extendedColors.customAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = taskTitle,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = extendedColors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Use Template Button
                    Button(
                        onClick = {
                            viewModel.useTemplate(template)
                            selectedTemplateForDetail = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("use_template_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extendedColors.customAccent,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Use Template →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Create Custom Template Dialog
    if (showCreateCustomTemplateDialog) {
        var templateName by remember { mutableStateOf("") }
        var templateDesc by remember { mutableStateOf("") }
        var templateCategory by remember { mutableStateOf("Personal") }
        var taskItemsList by remember { mutableStateOf(listOf("First milestone", "Second milestone")) }
        var newTaskText by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = { showCreateCustomTemplateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Custom Template", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                        IconButton(onClick = { showCreateCustomTemplateDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = extendedColors.textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = templateName,
                                onValueChange = { templateName = it },
                                placeholder = { Text("Template Name (e.g. Evening Coding)") },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = templateDesc,
                                onValueChange = { templateDesc = it },
                                placeholder = { Text("Short Description") },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Text("Tasks in Template (${taskItemsList.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            taskItemsList.forEachIndexed { idx, itemText ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("• $itemText", fontSize = 14.sp, color = extendedColors.textPrimary, modifier = Modifier.weight(1f))
                                    IconButton(onClick = {
                                        taskItemsList = taskItemsList.toMutableList().also { it.removeAt(idx) }
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = extendedColors.textTertiary)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newTaskText,
                                    onValueChange = { newTaskText = it },
                                    placeholder = { Text("Add task step...") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (newTaskText.isNotBlank()) {
                                            taskItemsList = taskItemsList + newTaskText.trim()
                                            newTaskText = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(extendedColors.customAccent)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (templateName.isNotBlank() && taskItemsList.isNotEmpty()) {
                                viewModel.createCustomTemplate(
                                    name = templateName,
                                    description = templateDesc.ifBlank { "Custom productivity routine" },
                                    category = templateCategory,
                                    iconName = "star",
                                    colorHex = "#7C68EE",
                                    taskTitles = taskItemsList,
                                    estimatedMinutes = 30
                                )
                                showCreateCustomTemplateDialog = false
                            }
                        },
                        enabled = templateName.isNotBlank() && taskItemsList.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = extendedColors.customAccent)
                    ) {
                        Text("Save Template", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
