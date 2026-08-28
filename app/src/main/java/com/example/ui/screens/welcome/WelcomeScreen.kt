package com.example.ui.screens.welcome

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderLight
import com.example.ui.theme.PastelYellow
import com.example.ui.theme.PastelYellowLight

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSetupReminder: (enabled: Boolean, time: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current

    var step by remember { mutableStateOf(1) } // 1: Welcome Hero, 2: Permissions, 3: Daily Planning
    var selectedReminderTime by remember { mutableStateOf("08:00 AM") }
    var reminderEnabled by remember { mutableStateOf(true) }

    // Check notification permission state dynamically
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    // Check exact alarm permission state
    val canScheduleExactAlarms by remember {
        derivedStateOf {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                alarmManager?.canScheduleExactAlarms() ?: true
            } else true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            step = 3
        }
    }

    Scaffold(
        containerColor = extendedColors.subtleBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step Progress Indicator
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("welcome_step_indicator"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(1, 2, 3).forEach { index ->
                    val isCurrent = step == index
                    val isPassed = step > index
                    Box(
                        modifier = Modifier
                            .width(if (isCurrent) 28.dp else 8.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> extendedColors.customAccent
                                    isPassed -> extendedColors.customAccent.copy(alpha = 0.5f)
                                    else -> extendedColors.cardBorder
                                }
                            )
                    )
                }
            }

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                },
                label = "welcome_step_content",
                modifier = Modifier.weight(1f, fill = false)
            ) { currentStep ->
                when (currentStep) {
                    1 -> {
                        // Step 1: Brand & Hero Showcase (Modern Vector App Icon Logo)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Modern 3D-styled App Icon Emblem
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .shadow(
                                        elevation = 18.dp,
                                        shape = RoundedCornerShape(28.dp),
                                        spotColor = extendedColors.customAccent.copy(alpha = 0.4f),
                                        ambientColor = extendedColors.customAccent.copy(alpha = 0.2f)
                                    )
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                extendedColors.customAccent,
                                                extendedColors.customAccent.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = Color.White.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(28.dp)
                                    )
                                    .testTag("welcome_app_logo"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Inner Floating Plate
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color.White)
                                        .shadow(4.dp, RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Toodly Logo Checkmark",
                                        tint = extendedColors.customAccent,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Toodly",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = extendedColors.textPrimary,
                                letterSpacing = (-0.5).sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Plan Today, Achieve Tomorrow",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = extendedColors.textSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            // Interactive Task Card Preview
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = extendedColors.customAccent.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                                border = BorderStroke(1.dp, extendedColors.cardBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Today's Focus",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = extendedColors.textPrimary
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PastelYellowLight)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("3 Tasks", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PastelYellow)
                                        }
                                    }

                                    // Mock Task 1
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(extendedColors.subtleBackground)
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(extendedColors.customAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Text(
                                            text = "Morning Planning & Review",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = extendedColors.textPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(extendedColors.customAccent)
                                        )
                                    }

                                    // Mock Task 2
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(extendedColors.subtleBackground)
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .border(1.5.dp, extendedColors.textTertiary, CircleShape)
                                        )
                                        Text(
                                            text = "Finalize Design Specifications",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = extendedColors.textPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "02:00 PM",
                                            fontSize = 11.sp,
                                            color = extendedColors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Full Notification & Reminders Permission Flow
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(extendedColors.customAccentLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (hasNotificationPermission) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = extendedColors.customAccent,
                                    modifier = Modifier.size(46.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Enable Notifications",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Get on-time alerts, exact alarms, and daily planning summaries right when you need them.",
                                fontSize = 14.5.sp,
                                color = extendedColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Permission Details Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                                border = BorderStroke(1.dp, extendedColors.cardBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Item 1: Due date alerts
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(extendedColors.customAccentLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Alarm,
                                                contentDescription = null,
                                                tint = extendedColors.customAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Exact Due Alarms",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = extendedColors.textPrimary
                                            )
                                            Text(
                                                text = "Sound & vibration alerts scheduled to the exact minute",
                                                fontSize = 11.5.sp,
                                                color = extendedColors.textSecondary
                                            )
                                        }
                                        if (hasNotificationPermission) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Granted",
                                                tint = extendedColors.customAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Divider(color = extendedColors.cardBorder.copy(alpha = 0.6f))

                                    // Item 2: Morning Planning Digest
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(PastelYellowLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WbSunny,
                                                contentDescription = null,
                                                tint = PastelYellow,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Daily Morning Briefing",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = extendedColors.textPrimary
                                            )
                                            Text(
                                                text = "A gentle heads-up summary to organize your schedule",
                                                fontSize = 11.5.sp,
                                                color = extendedColors.textSecondary
                                            )
                                        }
                                        if (hasNotificationPermission) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Granted",
                                                tint = extendedColors.customAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Divider(color = extendedColors.cardBorder.copy(alpha = 0.6f))

                                    // Item 3: 100% Offline & Private
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(extendedColors.customAccentLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = extendedColors.customAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "100% Local & Private",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = extendedColors.textPrimary
                                            )
                                            Text(
                                                text = "All notifications run locally on device with zero cloud tracking",
                                                fontSize = 11.5.sp,
                                                color = extendedColors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            // Notification status indicator badge
                            if (hasNotificationPermission) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(extendedColors.customAccentLight)
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = extendedColors.customAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Full Notification Access Granted",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = extendedColors.customAccent
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Daily Planning Routine Setup
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(PastelYellowLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = PastelYellow,
                                    modifier = Modifier.size(46.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Daily Planning",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Start each morning with a quick 1-minute plan for a calm, productive day.",
                                fontSize = 14.5.sp,
                                color = extendedColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                                border = BorderStroke(1.dp, extendedColors.cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "Morning Daily Reminder",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = extendedColors.textPrimary
                                            )
                                            Text(
                                                "Brief review notification",
                                                fontSize = 12.sp,
                                                color = extendedColors.textSecondary
                                            )
                                        }
                                        Switch(
                                            checked = reminderEnabled,
                                            onCheckedChange = { reminderEnabled = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = extendedColors.customAccent
                                            )
                                        )
                                    }

                                    if (reminderEnabled) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            "Choose Preferred Time",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = extendedColors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("07:00 AM", "08:00 AM", "09:00 AM").forEach { time ->
                                                val isSelected = selectedReminderTime == time
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(if (isSelected) extendedColors.customAccent else extendedColors.subtleBackground)
                                                        .clickable { selectedReminderTime = time }
                                                        .padding(vertical = 10.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = time,
                                                        color = if (isSelected) Color.White else extendedColors.textPrimary,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.SemiBold
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

            // Bottom Action Navigation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        when (step) {
                            1 -> step = 2
                            2 -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (!hasNotificationPermission) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        step = 3
                                    }
                                } else {
                                    step = 3
                                }
                            }
                            3 -> {
                                onSetupReminder(reminderEnabled, selectedReminderTime)
                                onGetStarted()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("welcome_get_started_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.customAccent,
                        contentColor = Color.White
                    )
                ) {
                    val buttonText = when (step) {
                        1 -> "Get Started →"
                        2 -> if (hasNotificationPermission) "Continue →" else "Grant Full Notification Access"
                        3 -> "Start Planning ✨"
                        else -> "Continue"
                    }
                    Text(
                        text = buttonText,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (step == 2 || step == 3) {
                    TextButton(
                        onClick = {
                            if (step == 2) step = 3 else onGetStarted()
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (step == 3) "Skip for now" else "Maybe Later",
                            color = extendedColors.textSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
