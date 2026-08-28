package com.example.ui.screens.settings

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.viewmodel.ToodlyViewModel

@Composable
fun SettingsScreen(
    viewModel: ToodlyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current

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

    var notificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationPermissionGranted = isGranted
        if (isGranted) {
            viewModel.sendTestNotification()
        }
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
                text = "Settings",
                fontSize = if (isCompact) 22.sp else 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = extendedColors.textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Customize themes, notifications, and compact mode",
                fontSize = if (isCompact) 13.sp else 15.sp,
                fontWeight = FontWeight.Normal,
                color = extendedColors.textSecondary
            )

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_list")
            ) {
                // Appearance & Themes Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 14.dp else 18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Appearance & Theme", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                                val currentPalette = AccentPalettes[accentColor]
                                Text(currentPalette?.displayName ?: accentColor, fontSize = 12.sp, color = extendedColors.customAccent, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Theme Mode Selector
                            Text("Canvas Mode", fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(6.dp))

                            val themeModes = listOf(
                                "SYSTEM" to "System",
                                "LIGHT" to "Light",
                                "DARK" to "Dark",
                                "CREAM" to "Cream Paper",
                                "OLED" to "OLED Black"
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(themeModes) { (modeKey, modeLabel) ->
                                    val isSelected = themeMode == modeKey
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                                            .clickable { viewModel.setThemeMode(modeKey) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                            .testTag("theme_button_$modeKey"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = modeLabel,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else extendedColors.textPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Accent Colors (10 Curated Palettes)
                            Text("Color Theme Palette", fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(AccentPalettes.entries.toList()) { (name, palette) ->
                                    val isSelected = accentColor == name
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) extendedColors.subtleBackground else Color.Transparent)
                                            .clickable { viewModel.setAccentColor(name) }
                                            .padding(horizontal = 6.dp, vertical = 6.dp)
                                            .testTag("accent_color_$name")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(palette.primary)
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 0.dp,
                                                    color = if (isSelected) extendedColors.textPrimary else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = palette.name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) extendedColors.customAccent else extendedColors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Compact Mode & Layout Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 14.dp else 18.dp)) {
                            Text("Display & Layout Density", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Compact Mode", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textPrimary)
                                        if (compactMode) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(extendedColors.customAccentLight)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = extendedColors.customAccent)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Dense layout, tighter spacing, and streamlined task cards for small display phones", fontSize = 12.sp, color = extendedColors.textSecondary)
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

                // Push Notifications & Reminders Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 14.dp else 18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Push Notifications & Reminders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (notificationPermissionGranted) extendedColors.customAccentLight else MaterialTheme.colorScheme.errorContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (notificationPermissionGranted) "Ready 🔔" else "Permission Needed",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (notificationPermissionGranted) extendedColors.customAccent else MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Receive timely on-device alerts when your to-do items are due.", fontSize = 12.sp, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Send Test Notification Button
                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.sendTestNotification()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("send_test_notification_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = extendedColors.customAccent)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Test Push Notification Now 🚀", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = extendedColors.cardBorder)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Daily Planning reminder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Daily Morning Planning", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = extendedColors.textPrimary)
                                    Text("Schedule review reminder at $dailyReminderTime", fontSize = 12.sp, color = extendedColors.textSecondary)
                                }
                                Switch(
                                    checked = dailyReminderEnabled,
                                    onCheckedChange = { enabled ->
                                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                        viewModel.setDailyReminder(enabled, dailyReminderTime)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = extendedColors.customAccent
                                    ),
                                    modifier = Modifier.testTag("daily_reminder_switch")
                                )
                            }
                        }
                    }
                }

                // General Preferences Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 14.dp else 18.dp)) {
                            Text("Calendar & Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
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
                        }
                    }
                }

                // Data & Privacy Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(if (isCompact) 14.dp else 18.dp)) {
                            Text("Data & Privacy", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Your tasks are stored strictly offline on your device.", fontSize = 12.sp, color = extendedColors.textSecondary)
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
                                    .padding(vertical = 8.dp),
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
                                    .padding(vertical = 8.dp),
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
                                    .padding(vertical = 8.dp),
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
                        shape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp),
                        colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(if (isCompact) 14.dp else 18.dp),
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Toodly v1.1", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
                            Text("100% Free, Offline & Open Task Manager", fontSize = 12.sp, color = extendedColors.textSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Zero ads, zero subscriptions. Beautiful aesthetic pastel themes & push notifications.",
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
