package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.Screen
import com.example.ui.navigation.bottomNavItems
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.stats.StatsScreen
import com.example.ui.screens.templates.TemplatesScreen
import com.example.ui.screens.welcome.WelcomeScreen
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ToodlyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ToodlyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val accentColor by viewModel.accentColor.collectAsState()
            val compactMode by viewModel.compactMode.collectAsState()
            val isWelcomeCompleted by viewModel.isWelcomeCompleted.collectAsState()

            MyApplicationTheme(
                themeMode = themeMode,
                accentName = accentColor,
                compactMode = compactMode
            ) {
                val extendedColors = LocalExtendedColors.current
                val navController = rememberNavController()

                Crossfade(targetState = isWelcomeCompleted, label = "welcomeCrossfade") { isCompleted ->
                    if (!isCompleted) {
                        WelcomeScreen(
                            onGetStarted = {
                                viewModel.setWelcomeCompleted(true)
                            },
                            onSetupReminder = { enabled, time ->
                                viewModel.setDailyReminder(enabled, time)
                            }
                        )
                    } else {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        Scaffold(
                            containerColor = MaterialTheme.colorScheme.background,
                            bottomBar = {
                                NavigationBar(
                                    containerColor = extendedColors.cardBackground,
                                    tonalElevation = 6.dp,
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .testTag("main_bottom_nav")
                                ) {
                                    bottomNavItems.forEach { item ->
                                        val isSelected = currentRoute == item.route

                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                if (currentRoute != item.route) {
                                                    navController.navigate(item.route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = item.title,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = extendedColors.customAccent,
                                                selectedTextColor = extendedColors.customAccent,
                                                indicatorColor = extendedColors.customAccentLight,
                                                unselectedIconColor = extendedColors.textTertiary,
                                                unselectedTextColor = extendedColors.textSecondary
                                            ),
                                            modifier = Modifier.testTag(item.testTag)
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Home.route,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable(Screen.Home.route) {
                                    HomeScreen(viewModel = viewModel)
                                }
                                composable(Screen.Calendar.route) {
                                    CalendarScreen(viewModel = viewModel)
                                }
                                composable(Screen.Templates.route) {
                                    TemplatesScreen(
                                        viewModel = viewModel,
                                        onSelectTemplate = {}
                                    )
                                }
                                composable(Screen.Stats.route) {
                                    StatsScreen(viewModel = viewModel)
                                }
                                composable(Screen.Settings.route) {
                                    SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
