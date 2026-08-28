package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ToodlyDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.Priority
import com.example.data.model.Recurrence
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.data.model.TemplateEntity
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.TaskRepository
import com.example.reminder.ReminderManager
import com.example.ui.components.DayOverview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HomeTab {
    TODAY, UPCOMING, COMPLETED
}

enum class CalendarViewMode {
    MONTH, WEEK, DAY
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedPriority: String? = null,
    val showOnlyOverdue: Boolean = false
)

class ToodlyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ToodlyDatabase.getInstance(application)
    private val repository = TaskRepository(db.taskDao(), db.categoryDao(), db.templateDao())
    private val preferencesRepository = PreferencesRepository(application)
    private val reminderManager = ReminderManager(application)

    val isWelcomeCompleted: StateFlow<Boolean> = preferencesRepository.isWelcomeCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val themeMode: StateFlow<String> = preferencesRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val accentColor: StateFlow<String> = preferencesRepository.accentColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Lavender")

    val completionAnimation: StateFlow<String> = preferencesRepository.completionAnimation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Bounce")

    val weekStartDay: StateFlow<String> = preferencesRepository.weekStartDay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Monday")

    val dailyReminderEnabled: StateFlow<Boolean> = preferencesRepository.dailyReminderEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val dailyReminderTime: StateFlow<String> = preferencesRepository.dailyReminderTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "08:00 AM")

    val userName: StateFlow<String> = preferencesRepository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Friend")

    val compactMode: StateFlow<Boolean> = preferencesRepository.compactMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTemplates: StateFlow<List<TemplateEntity>> = repository.allTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & Interaction States
    private val _currentHomeTab = MutableStateFlow(HomeTab.TODAY)
    val currentHomeTab: StateFlow<HomeTab> = _currentHomeTab.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(getTodayDateString())
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    private val _calendarViewMode = MutableStateFlow(CalendarViewMode.MONTH)
    val calendarViewMode: StateFlow<CalendarViewMode> = _calendarViewMode.asStateFlow()

    private val _recentlyDeletedTask = MutableStateFlow<TaskItem?>(null)
    val recentlyDeletedTask: StateFlow<TaskItem?> = _recentlyDeletedTask.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Filtered Tasks for Home
    val filteredTasks: StateFlow<List<TaskItem>> = combine(
        allTasks,
        _currentHomeTab,
        _filterState
    ) { tasks, tab, filter ->
        val todayStr = getTodayDateString()
        tasks.filter { task ->
            // Tab condition
            val matchesTab = when (tab) {
                HomeTab.TODAY -> {
                    // Today includes tasks due today OR overdue uncompleted tasks OR unscheduled uncompleted tasks
                    if (task.isCompleted) {
                        task.dueDate == todayStr
                    } else {
                        task.dueDate.isEmpty() || task.dueDate <= todayStr
                    }
                }
                HomeTab.UPCOMING -> {
                    !task.isCompleted && task.dueDate.isNotEmpty() && task.dueDate > todayStr
                }
                HomeTab.COMPLETED -> {
                    task.isCompleted
                }
            }

            // Search query condition
            val matchesSearch = if (filter.searchQuery.isBlank()) true else {
                task.title.contains(filter.searchQuery, ignoreCase = true) ||
                        task.notes.contains(filter.searchQuery, ignoreCase = true) ||
                        task.category.contains(filter.searchQuery, ignoreCase = true)
            }

            // Category condition
            val matchesCategory = filter.selectedCategory == null || task.category.equals(filter.selectedCategory, ignoreCase = true)

            // Priority condition
            val matchesPriority = filter.selectedPriority == null || task.priority.equals(filter.selectedPriority, ignoreCase = true)

            // Overdue condition
            val matchesOverdue = if (filter.showOnlyOverdue) {
                !task.isCompleted && task.dueDate.isNotEmpty() && task.dueDate < todayStr
            } else true

            matchesTab && matchesSearch && matchesCategory && matchesPriority && matchesOverdue
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics Calculations
    val todayCompletedCount: StateFlow<Int> = allTasks.combine(_selectedCalendarDate) { tasks, _ ->
        val today = getTodayDateString()
        tasks.count { it.dueDate == today && it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayTotalCount: StateFlow<Int> = allTasks.combine(_selectedCalendarDate) { tasks, _ ->
        val today = getTodayDateString()
        tasks.count { it.dueDate == today || (it.dueDate.isEmpty() && !it.isCompleted) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Weekly Statistics Calculations for Weekly Overview
    val weeklyDayStats: StateFlow<List<DayOverview>> = combine(allTasks, weekStartDay) { tasks, startDay ->
        val weekCalendars = getWeekDates(startDay)
        val todayStr = getTodayDateString()
        val daySdf = SimpleDateFormat("EEE", Locale.getDefault())
        val dateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        weekCalendars.map { cal ->
            val dateStr = dateSdf.format(cal.time)
            val dayAbbr = daySdf.format(cal.time)
            val dayLetter = dayAbbr.take(1)
            val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val dayTasks = tasks.filter { it.dueDate == dateStr }
            val compCount = dayTasks.count { it.isCompleted }
            val totCount = dayTasks.size

            DayOverview(
                dayAbbreviation = dayAbbr,
                dayLetter = dayLetter,
                dateString = dateStr,
                dayOfMonth = dayOfMonth,
                completedCount = compCount,
                totalCount = totCount,
                isToday = dateStr == todayStr
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyCompletedCount: StateFlow<Int> = weeklyDayStats.combine(allTasks) { days, _ ->
        days.sumOf { it.completedCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyTotalCount: StateFlow<Int> = weeklyDayStats.combine(allTasks) { days, _ ->
        days.sumOf { it.totalCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyCompletionRate: StateFlow<Int> = combine(weeklyCompletedCount, weeklyTotalCount) { comp, tot ->
        if (tot > 0) ((comp.toFloat() / tot.toFloat()) * 100).toInt() else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentWeekRangeLabel: StateFlow<String> = weekStartDay.combine(allTasks) { startDay, _ ->
        val weekCalendars = getWeekDates(startDay)
        if (weekCalendars.isNotEmpty()) {
            val monthSdf = SimpleDateFormat("MMM d", Locale.getDefault())
            "${monthSdf.format(weekCalendars.first().time)} - ${monthSdf.format(weekCalendars.last().time)}"
        } else "This Week"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "This Week")

    init {
        // Create demo tasks if empty on first start
        viewModelScope.launch {
            allTasks.collect { list ->
                if (list.isEmpty()) {
                    populateInitialTasks()
                }
            }
        }
    }

    private suspend fun populateInitialTasks() {
        val today = getTodayDateString()
        val tomorrow = getOffsetDateString(1)
        val initialList = listOf(
            TaskItem(
                title = "Workout & Stretching",
                dueTime = "8:00 AM",
                dueDate = today,
                priority = Priority.HIGH.name,
                category = "Fitness",
                isCompleted = false
            ),
            TaskItem(
                title = "Read 20 minutes",
                dueTime = "9:00 AM",
                dueDate = today,
                priority = Priority.MEDIUM.name,
                category = "Personal",
                isCompleted = true,
                completedAt = System.currentTimeMillis() - 3600000
            ),
            TaskItem(
                title = "Plan project milestones",
                dueTime = "10:00 AM",
                dueDate = today,
                priority = Priority.HIGH.name,
                category = "Work",
                isCompleted = false,
                subtasksJson = Subtask.serializeList(
                    listOf(
                        Subtask(title = "Define user stories", isCompleted = true),
                        Subtask(title = "Estimate sprint tasks", isCompleted = false)
                    )
                )
            ),
            TaskItem(
                title = "Grocery shopping",
                dueTime = "6:00 PM",
                dueDate = today,
                priority = Priority.LOW.name,
                category = "Groceries",
                isCompleted = false
            ),
            TaskItem(
                title = "Team weekly meeting",
                dueTime = "2:30 PM",
                dueDate = tomorrow,
                priority = Priority.MEDIUM.name,
                category = "Work",
                isCompleted = false
            )
        )
        repository.insertTasks(initialList)
    }

    fun setHomeTab(tab: HomeTab) {
        _currentHomeTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun toggleCategoryFilter(category: String?) {
        _filterState.value = _filterState.value.copy(
            selectedCategory = if (_filterState.value.selectedCategory == category) null else category
        )
    }

    fun togglePriorityFilter(priority: String?) {
        _filterState.value = _filterState.value.copy(
            selectedPriority = if (_filterState.value.selectedPriority == priority) null else priority
        )
    }

    fun toggleOverdueFilter() {
        _filterState.value = _filterState.value.copy(
            showOnlyOverdue = !_filterState.value.showOnlyOverdue
        )
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun setSelectedCalendarDate(dateStr: String) {
        _selectedCalendarDate.value = dateStr
    }

    fun setCalendarViewMode(mode: CalendarViewMode) {
        _calendarViewMode.value = mode
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun toggleCompactMode() {
        viewModelScope.launch {
            val current = compactMode.value
            preferencesRepository.setCompactMode(!current)
            _toastMessage.value = if (!current) "Compact mode enabled" else "Standard mode enabled"
        }
    }

    fun sendTestNotification() {
        reminderManager.sendImmediateTestNotification(
            title = "Toodly Reminder Alert 🎯",
            message = "Your push notifications are fully working! Don't forget your tasks."
        )
        _toastMessage.value = "Test notification sent!"
    }

    fun areNotificationsEnabled(): Boolean {
        return reminderManager.areNotificationsEnabled()
    }

    fun quickAddTask(
        title: String,
        category: String = "Personal",
        dueDate: String = getTodayDateString(),
        dueTime: String = "9:00 AM",
        priority: String = "MEDIUM",
        hasReminder: Boolean = false
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val reminderTs = if (hasReminder) {
                ReminderManager.parseDateAndTimeToMillis(dueDate, dueTime) ?: (System.currentTimeMillis() + 3600000)
            } else null

            val task = TaskItem(
                title = title.trim(),
                category = category,
                dueDate = dueDate,
                dueTime = dueTime,
                priority = priority,
                hasReminder = hasReminder,
                reminderTimestamp = reminderTs,
                createdAt = System.currentTimeMillis()
            )
            val id = repository.insertTask(task)
            if (hasReminder && reminderTs != null) {
                reminderManager.scheduleTaskReminder(id, task.title, task.category, reminderTs)
            }
            _toastMessage.value = if (hasReminder) "Task created with reminder 🔔" else "Task created"
        }
    }

    fun saveTask(task: TaskItem) {
        viewModelScope.launch {
            val reminderTs = if (task.hasReminder) {
                task.reminderTimestamp ?: ReminderManager.parseDateAndTimeToMillis(task.dueDate, task.dueTime) ?: (System.currentTimeMillis() + 3600000)
            } else null

            val taskToSave = task.copy(reminderTimestamp = reminderTs)

            if (taskToSave.id == 0L) {
                val newId = repository.insertTask(taskToSave)
                if (taskToSave.hasReminder && reminderTs != null) {
                    reminderManager.scheduleTaskReminder(newId, taskToSave.title, taskToSave.category, reminderTs)
                }
                _toastMessage.value = if (taskToSave.hasReminder) "Task created with reminder 🔔" else "Task created"
            } else {
                repository.updateTask(taskToSave)
                if (taskToSave.hasReminder && reminderTs != null) {
                    reminderManager.scheduleTaskReminder(taskToSave.id, taskToSave.title, taskToSave.category, reminderTs)
                } else {
                    reminderManager.cancelTaskReminder(taskToSave.id)
                }
                _toastMessage.value = "Task updated"
            }
        }
    }

    fun toggleTaskCompletion(task: TaskItem) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
            if (updated.isCompleted) {
                reminderManager.cancelTaskReminder(task.id)
            }
        }
    }

    fun duplicateTask(task: TaskItem) {
        viewModelScope.launch {
            val duplicate = task.copy(
                id = 0,
                title = "${task.title} (Copy)",
                createdAt = System.currentTimeMillis(),
                isCompleted = false,
                completedAt = null
            )
            repository.insertTask(duplicate)
            _toastMessage.value = "Task duplicated"
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            _recentlyDeletedTask.value = task
            repository.deleteTask(task)
            reminderManager.cancelTaskReminder(task.id)
            _toastMessage.value = "Task deleted"
        }
    }

    fun undoDelete() {
        val taskToRestore = _recentlyDeletedTask.value ?: return
        viewModelScope.launch {
            repository.insertTask(taskToRestore)
            _recentlyDeletedTask.value = null
            _toastMessage.value = "Task restored"
        }
    }

    fun useTemplate(template: TemplateEntity, targetDate: String = getTodayDateString()) {
        viewModelScope.launch {
            val taskTitles = template.getTaskList()
            val newTasks = taskTitles.mapIndexed { index, title ->
                TaskItem(
                    title = title,
                    category = template.category,
                    dueDate = targetDate,
                    dueTime = "${9 + (index * 2 % 10)}:00 AM",
                    priority = Priority.MEDIUM.name,
                    createdAt = System.currentTimeMillis() + index
                )
            }
            repository.insertTasks(newTasks)
            _toastMessage.value = "Added ${newTasks.size} tasks from ${template.name}"
        }
    }

    fun createCustomTemplate(name: String, description: String, category: String, iconName: String, colorHex: String, taskTitles: List<String>, estimatedMinutes: Int) {
        viewModelScope.launch {
            val template = TemplateEntity(
                name = name.trim(),
                description = description.trim(),
                category = category,
                iconName = iconName,
                colorHex = colorHex,
                estimatedTimeMinutes = estimatedMinutes,
                tasksJson = TemplateEntity.serializeTaskList(taskTitles.filter { it.isNotBlank() }),
                isCustom = true
            )
            repository.insertTemplate(template)
            _toastMessage.value = "Custom template saved"
        }
    }

    fun deleteTemplate(template: TemplateEntity) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
            _toastMessage.value = "Template deleted"
        }
    }

    fun addNewCategory(name: String, iconName: String, colorHex: String) {
        viewModelScope.launch {
            val category = CategoryEntity(name = name.trim(), iconName = iconName, colorHex = colorHex)
            repository.insertCategory(category)
            _toastMessage.value = "Category added"
        }
    }

    // Settings actions
    fun setWelcomeCompleted(completed: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setWelcomeCompleted(completed)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setAccentColor(color: String) {
        viewModelScope.launch {
            preferencesRepository.setAccentColor(color)
        }
    }

    fun setCompletionAnimation(anim: String) {
        viewModelScope.launch {
            preferencesRepository.setCompletionAnimation(anim)
        }
    }

    fun setWeekStartDay(day: String) {
        viewModelScope.launch {
            preferencesRepository.setWeekStartDay(day)
        }
    }

    fun setDailyReminder(enabled: Boolean, timeStr: String) {
        viewModelScope.launch {
            preferencesRepository.setDailyReminderEnabled(enabled)
            preferencesRepository.setDailyReminderTime(timeStr)
            if (enabled) {
                // parse time
                val parts = timeStr.split(":", " ")
                var hour = parts[0].toIntOrNull() ?: 8
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                val amPm = parts.getOrNull(2) ?: "AM"
                if (amPm.equals("PM", ignoreCase = true) && hour < 12) hour += 12
                if (amPm.equals("AM", ignoreCase = true) && hour == 12) hour = 0
                reminderManager.scheduleDailyPlanning(hour, minute)
            } else {
                reminderManager.cancelDailyPlanning()
            }
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setUserName(name.trim())
        }
    }

    fun setCompactMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setCompactMode(enabled)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllTasks()
            _toastMessage.value = "All tasks cleared"
        }
    }

    fun exportTasksJson(): String {
        return repository.exportTasksToJson(allTasks.value)
    }

    fun importTasksJson(jsonString: String) {
        viewModelScope.launch {
            val count = repository.importTasksFromJson(jsonString)
            _toastMessage.value = if (count > 0) "Imported $count tasks successfully" else "Invalid JSON format"
        }
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun getOffsetDateString(daysOffset: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(calendar.time)
        }

        fun getWeekDates(startDayPref: String = "Monday"): List<Calendar> {
            val cal = Calendar.getInstance()
            val isSundayStart = startDayPref.equals("Sunday", ignoreCase = true)
            cal.firstDayOfWeek = if (isSundayStart) Calendar.SUNDAY else Calendar.MONDAY

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val firstDay = cal.firstDayOfWeek
            val diff = (dayOfWeek - firstDay + 7) % 7
            cal.add(Calendar.DAY_OF_YEAR, -diff)

            val week = mutableListOf<Calendar>()
            for (i in 0 until 7) {
                val dayCal = cal.clone() as Calendar
                week.add(dayCal)
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            return week
        }
    }
}
