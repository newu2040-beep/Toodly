package com.example.ui.screens.welcome

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderLight

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSetupReminder: (enabled: Boolean, time: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current

    var step by remember { mutableStateOf(1) } // 1: Welcome, 2: Notifications, 3: Daily Planning
    var selectedReminderTime by remember { mutableStateOf("08:00 AM") }
    var reminderEnabled by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        step = 3
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
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 2, 3).forEach { index ->
                    Box(
                        modifier = Modifier
                            .width(if (step == index) 28.dp else 8.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(if (step == index) extendedColors.customAccent else extendedColors.cardBorder)
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
                label = "welcomeStep"
            ) { currentStep ->
                when (currentStep) {
                    1 -> {
                        // Step 1: Brand & Hero Illustration
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Toodly Logo Badge
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(extendedColors.customAccentLight)
                                    .border(1.5.dp, extendedColors.customAccent.copy(alpha = 0.3f), RoundedCornerShape(22.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = extendedColors.customAccent,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Toodly",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = extendedColors.textPrimary,
                                letterSpacing = (-0.5).sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Plan Today,\nAchieve Tomorrow",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = extendedColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // 3D Clipboard Card Illustration
                            Card(
                                modifier = Modifier
                                    .size(240.dp)
                                    .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = extendedColors.customAccent.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.welcome_illustration),
                                    contentDescription = "Welcome task clipboard",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Notifications Permission
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(extendedColors.customAccentLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = extendedColors.customAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Stay on Track",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Get gentle reminders when tasks are due so you never miss an important moment.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = extendedColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                                border = androidx.compose.foundation.BorderStroke(1.dp, extendedColors.cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "✓ 100% Offline & Private",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = extendedColors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✓ Zero Ads, Zero Spam, Always Free",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = extendedColors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✓ Full control in app settings",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = extendedColors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Daily Planning Routine
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(extendedColors.customAccentLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = extendedColors.customAccent,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Daily Planning",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Start each morning with a quick 1-minute plan for a calm, productive day.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = extendedColors.textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                                border = androidx.compose.foundation.BorderStroke(1.dp, extendedColors.cardBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Daily Reminder", fontWeight = FontWeight.Bold, color = extendedColors.textPrimary)
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
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Choose Time", fontSize = 13.sp, color = extendedColors.textSecondary)
                                        Spacer(modifier = Modifier.height(6.dp))
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
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = time,
                                                        color = if (isSelected) Color.White else extendedColors.textPrimary,
                                                        fontSize = 12.sp,
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

            // Bottom Navigation Action Button
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
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (!hasPermission) {
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
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.customAccent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (step == 3) "Start Planning ✨" else if (step == 2) "Enable Notifications" else "Get Started →",
                        fontSize = 17.sp,
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
