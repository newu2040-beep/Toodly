package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Templates : Screen("templates")
    object TemplateDetail : Screen("template_detail/{templateId}") {
        fun createRoute(templateId: Long) = "template_detail/$templateId"
    }
    object Stats : Screen("stats")
    object Settings : Screen("settings")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    ),
    BottomNavItem(
        route = Screen.Calendar.route,
        title = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        testTag = "nav_calendar"
    ),
    BottomNavItem(
        route = Screen.Templates.route,
        title = "Templates",
        selectedIcon = Icons.Filled.FormatListBulleted,
        unselectedIcon = Icons.Outlined.FormatListBulleted,
        testTag = "nav_templates"
    ),
    BottomNavItem(
        route = Screen.Stats.route,
        title = "Stats",
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights,
        testTag = "nav_stats"
    ),
    BottomNavItem(
        route = Screen.Settings.route,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        testTag = "nav_settings"
    )
)
