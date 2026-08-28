package com.example.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.theme.AccentPalettes
import com.example.ui.theme.LocalExtendedColors
import com.example.viewmodel.ToodlyViewModel

@Composable
fun SettingsScreen(
    viewModel: ToodlyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current

    val themeMode by viewModel.themeMode.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val completionAnimation by viewModel.completionAnimation.collectAsState()
    val weekStartDay by viewModel.weekStartDay.collectAsState()
    val dailyReminderEnabled by viewModel.dailyReminderEnabled.collectAsState()
    val dailyReminderTime by viewModel.dailyReminderTime.collectAsState()
    val compactMode by viewModel.compactMode.collectAsState()

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Settings",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = extendedColors.textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Customize your experience and manage data",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = extendedColors.textSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_list")
            ) {
                // Appearance Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Appearance & Theme", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Theme Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("SYSTEM" to "System", "LIGHT" to "Light", "DARK" to "Dark").forEach { (modeKey, modeLabel) ->
                                    val isSelected = themeMode == modeKey
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                                            .clickable { viewModel.setThemeMode(modeKey) }
                                            .padding(vertical = 10.dp)
                                            .testTag("theme_button_$modeKey"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = modeLabel,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else extendedColors.textPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Accent Colors
                            Text("Accent Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(AccentPalettes.entries.toList()) { (name, palette) ->
                                    val isSelected = accentColor == name
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(palette.primary)
                                            .border(
                                                width = if (isSelected) 3.dp else 0.dp,
                                                color = if (isSelected) extendedColors.textPrimary else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { viewModel.setAccentColor(name) }
                                            .testTag("accent_color_$name"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Preferences Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Week start day
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Week Starts On", fontSize = 14.sp, color = extendedColors.textPrimary)
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(extendedColors.subtleBackground)
                                        .padding(2.dp)
                                ) {
                                    listOf("Monday", "Sunday").forEach { day ->
                                        val isSelected = weekStartDay == day
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) extendedColors.customAccent else Color.Transparent)
                                                .clickable { viewModel.setWeekStartDay(day) }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = day,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color.White else extendedColors.textSecondary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Daily Planning reminder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Daily Morning Planning", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = extendedColors.textPrimary)
                                    Text("Reminder at $dailyReminderTime", fontSize = 12.sp, color = extendedColors.textSecondary)
                                }
                                Switch(
                                    checked = dailyReminderEnabled,
                                    onCheckedChange = { enabled ->
                                        viewModel.setDailyReminder(enabled, dailyReminderTime)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = extendedColors.customAccent
                                    ),
                                    modifier = Modifier.testTag("daily_reminder_switch")
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Compact Mode for small display phones
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Text("Compact Mode", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = extendedColors.textPrimary)
                                    Text("Dense layout & smaller cards for small displays", fontSize = 12.sp, color = extendedColors.textSecondary)
                                }
                                Switch(
                                    checked = compactMode,
                                    onCheckedChange = { enabled ->
                                        viewModel.setCompactMode(enabled)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = extendedColors.customAccent
                                    ),
                                    modifier = Modifier.testTag("compact_mode_switch")
                                )
                            }
                        }
                    }
                }

                // Data & Privacy Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Data & Privacy", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Your tasks are stored strictly on your device.", fontSize = 12.sp, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Export JSON
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        exportedJsonText = viewModel.exportTasksJson()
                                        showExportDialog = true
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = extendedColors.customAccent)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Export Tasks (JSON)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textPrimary)
                                    Text("Create a backup of all tasks", fontSize = 12.sp, color = extendedColors.textSecondary)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = extendedColors.textTertiary)
                            }

                            Divider(color = extendedColors.cardBorder, modifier = Modifier.padding(vertical = 4.dp))

                            // Import JSON
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showImportDialog = true }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.FileUpload, contentDescription = null, tint = extendedColors.customAccent)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Import Tasks (JSON)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textPrimary)
                                    Text("Restore tasks from a backup", fontSize = 12.sp, color = extendedColors.textSecondary)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = extendedColors.textTertiary)
                            }

                            Divider(color = extendedColors.cardBorder, modifier = Modifier.padding(vertical = 4.dp))

                            // Clear All Tasks
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showClearDataDialog = true }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Clear All Tasks", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                                    Text("Permanently remove all tasks", fontSize = 12.sp, color = extendedColors.textSecondary)
                                }
                            }
                        }
                    }
                }

                // About Toodly Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(extendedColors.customAccentLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = extendedColors.customAccent)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Toodly v1.0", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Text("100% Free & Open Task Manager", fontSize = 12.sp, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No paywalls, no subscriptions, no ads. Built for effortless daily productivity.",
                                fontSize = 12.sp,
                                color = extendedColors.textTertiary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exported Tasks JSON", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Copy your exported backup below:", fontSize = 13.sp, color = extendedColors.textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Toodly Tasks Backup", exportedJsonText)
                        clipboard.setPrimaryClip(clip)
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = extendedColors.customAccent)
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Tasks JSON", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste your exported JSON tasks backup below:", fontSize = 13.sp, color = extendedColors.textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        placeholder = { Text("Paste JSON here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonInput.isNotBlank()) {
                            viewModel.importTasksJson(importJsonInput.trim())
                            showImportDialog = false
                        }
                    },
                    enabled = importJsonInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = extendedColors.customAccent)
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Tasks?") },
            text = { Text("This will permanently delete all tasks from the local database. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDataDialog = false
                        viewModel.clearAllData()
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
