package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WbSunny
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
import com.example.data.model.CategoryEntity
import com.example.data.model.TaskItem
import com.example.ui.components.QuickAddSheet
import com.example.ui.components.TaskCard
import com.example.ui.components.getCategoryPastelColors
import com.example.ui.screens.taskdetail.TaskDetailDialog
import com.example.ui.theme.LocalCompactMode
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderLight
import com.example.ui.theme.PastelYellow
import com.example.ui.theme.PastelYellowLight
import com.example.viewmodel.FilterState
import com.example.viewmodel.HomeTab
import com.example.viewmodel.ToodlyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ToodlyViewModel,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val isCompact = LocalCompactMode.current

    val tasks by viewModel.filteredTasks.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val currentTab by viewModel.currentHomeTab.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val todayCompleted by viewModel.todayCompletedCount.collectAsState()
    val todayTotal by viewModel.todayTotalCount.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showQuickAdd by remember { mutableStateOf(false) }
    var selectedTaskForDetail by remember { mutableStateOf<TaskItem?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            val message = toastMessage!!
            if (message == "Task deleted") {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                }
            } else {
                snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            }
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickAdd = true },
                containerColor = extendedColors.customAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("home_fab_add_task")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Task", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showSearchBar = !showSearchBar },
                        modifier = Modifier.testTag("home_search_toggle")
                    ) {
                        Icon(
                            imageVector = if (showSearchBar) Icons.Default.Close else Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = extendedColors.textPrimary
                        )
                    }

                    Text(
                        text = "Toodly",
                        fontSize = if (isCompact) 20.sp else 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = extendedColors.textPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 34.dp else 40.dp)
                        .clip(CircleShape)
                        .background(extendedColors.customAccentLight)
                        .clickable { showProfileDialog = true }
                        .testTag("home_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = extendedColors.customAccent,
                        modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                    )
                }
            }

            // Search Field when toggled
            AnimatedVisibility(visible = showSearchBar) {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = filterState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search tasks, notes, categories...", color = extendedColors.textTertiary) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = extendedColors.customAccent) },
                        trailingIcon = {
                            if (filterState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = extendedColors.textTertiary)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_field"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = extendedColors.customAccent,
                            unfocusedBorderColor = extendedColors.cardBorder,
                            focusedContainerColor = extendedColors.cardBackground,
                            unfocusedContainerColor = extendedColors.cardBackground
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 14.dp))

            // Good Morning Progress Card (matching mockup)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_greeting_card"),
                shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
                colors = CardDefaults.cardColors(containerColor = extendedColors.cardBackground),
                border = BorderStroke(1.dp, extendedColors.cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isCompact) 12.dp else 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 38.dp else 48.dp)
                                .clip(RoundedCornerShape(if (isCompact) 12.dp else 16.dp))
                                .background(PastelYellowLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = PastelYellow,
                                modifier = Modifier.size(if (isCompact) 22.dp else 28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(if (isCompact) 10.dp else 14.dp))

                        Column {
                            Text(
                                text = "Good Morning, $userName!",
                                fontSize = if (isCompact) 15.sp else 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val remaining = (todayTotal - todayCompleted).coerceAtLeast(0)
                            Text(
                                text = if (remaining == 0 && todayTotal > 0) "All tasks completed! 🎉" else "$remaining tasks to complete today",
                                fontSize = if (isCompact) 11.5.sp else 13.sp,
                                color = extendedColors.textSecondary
                            )
                        }
                    }

                    // Circular or Pill progress
                    val progress = if (todayTotal > 0) todayCompleted.toFloat() / todayTotal.toFloat() else 0f
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 36.dp else 44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = extendedColors.customAccent,
                            trackColor = extendedColors.subtleBackground,
                            strokeWidth = if (isCompact) 3.5.dp else 4.dp
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = if (isCompact) 10.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 16.dp))

            // Main Section Tabs: Today | Upcoming | Completed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
                ) {
                    HomeTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        val label = when (tab) {
                            HomeTab.TODAY -> "Today"
                            HomeTab.UPCOMING -> "Upcoming"
                            HomeTab.COMPLETED -> "Completed"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(if (isCompact) 10.dp else 14.dp))
                                .background(if (isSelected) extendedColors.customAccent else Color.Transparent)
                                .clickable { viewModel.setHomeTab(tab) }
                                .padding(
                                    horizontal = if (isCompact) 10.dp else 14.dp,
                                    vertical = if (isCompact) 6.dp else 8.dp
                                )
                                .testTag("tab_${tab.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = if (isCompact) 13.sp else 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else extendedColors.textSecondary
                            )
                        }
                    }
                }

                TextButton(
                    onClick = { showQuickAdd = true },
                    modifier = Modifier.testTag("quick_add_text_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = extendedColors.customAccent, modifier = Modifier.size(if (isCompact) 16.dp else 18.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Add", color = extendedColors.customAccent, fontWeight = FontWeight.Bold, fontSize = if (isCompact) 12.sp else 13.sp)
                }
            }

            // Filter Pills Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isCompact) 4.dp else 10.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterState.showOnlyOverdue,
                        onClick = { viewModel.toggleOverdueFilter() },
                        label = { Text("Overdue", fontSize = if (isCompact) 11.sp else 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFECEC),
                            selectedLabelColor = Color(0xFFFF4D4D)
                        )
                    )
                }

                items(categories) { cat ->
                    val isSelected = filterState.selectedCategory.equals(cat.name, ignoreCase = true)
                    val (bgColor, textColor) = getCategoryPastelColors(cat.name, cat.colorHex)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleCategoryFilter(cat.name) },
                        label = { Text(cat.name, fontSize = if (isCompact) 11.sp else 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = bgColor,
                            selectedLabelColor = textColor
                        )
                    )
                }
            }

            // Task List
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 56.dp else 70.dp)
                                .clip(CircleShape)
                                .background(extendedColors.customAccentLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = extendedColors.customAccent,
                                modifier = Modifier.size(if (isCompact) 28.dp else 36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (currentTab) {
                                HomeTab.TODAY -> "No tasks due today!"
                                HomeTab.UPCOMING -> "No upcoming tasks planned."
                                HomeTab.COMPLETED -> "No completed tasks yet."
                            },
                            fontSize = if (isCompact) 15.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+ Add Task' to plan your day with ease.",
                            fontSize = if (isCompact) 13.sp else 14.sp,
                            color = extendedColors.textSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp),
                    contentPadding = PaddingValues(bottom = if (isCompact) 64.dp else 80.dp)
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleCompletion = { viewModel.toggleTaskCompletion(it) },
                            onClick = { selectedTaskForDetail = task },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    // Quick Add Modal Sheet
    if (showQuickAdd) {
        QuickAddSheet(
            categories = categories,
            onDismiss = { showQuickAdd = false },
            onAddTask = { title, category, dueDate, dueTime, priority ->
                viewModel.quickAddTask(title, category, dueDate, dueTime, priority)
            }
        )
    }

    // Full Task Detail Dialog
    if (selectedTaskForDetail != null) {
        TaskDetailDialog(
            task = selectedTaskForDetail!!,
            categories = categories,
            onDismiss = { selectedTaskForDetail = null },
            onSave = { updated -> viewModel.saveTask(updated) },
            onDuplicate = { viewModel.duplicateTask(it) },
            onDelete = { viewModel.deleteTask(it) }
        )
    }

    // Profile Dialog
    if (showProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Profile & Planning", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Customize your display name:", fontSize = 13.sp, color = extendedColors.textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setUserName(tempName)
                        showProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = extendedColors.customAccent)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
