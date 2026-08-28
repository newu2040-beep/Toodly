package com.example.data.repository

import com.example.data.local.CategoryDao
import com.example.data.local.TaskDao
import com.example.data.local.TemplateDao
import com.example.data.model.CategoryEntity
import com.example.data.model.TaskItem
import com.example.data.model.TemplateEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class TaskRepository(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao,
    private val templateDao: TemplateDao
) {
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allTemplates: Flow<List<TemplateEntity>> = templateDao.getAllTemplates()

    fun getTasksForDate(dateStr: String): Flow<List<TaskItem>> = taskDao.getTasksForDate(dateStr)

    suspend fun getTaskById(id: Long): TaskItem? = taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<TaskItem>): List<Long> = taskDao.insertTasks(tasks)

    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun clearAllTasks() = taskDao.clearAllTasks()

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun insertTemplate(template: TemplateEntity): Long = templateDao.insertTemplate(template)

    suspend fun deleteTemplate(template: TemplateEntity) = templateDao.deleteTemplate(template)

    // Export / Import support
    fun exportTasksToJson(tasks: List<TaskItem>): String {
        val array = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("notes", task.notes)
                put("isCompleted", task.isCompleted)
                put("completedAt", task.completedAt ?: JSONObject.NULL)
                put("createdAt", task.createdAt)
                put("dueDate", task.dueDate)
                put("dueTime", task.dueTime)
                put("dueTimestamp", task.dueTimestamp ?: JSONObject.NULL)
                put("priority", task.priority)
                put("category", task.category)
                put("hasReminder", task.hasReminder)
                put("reminderTimestamp", task.reminderTimestamp ?: JSONObject.NULL)
                put("recurrence", task.recurrence)
                put("subtasksJson", task.subtasksJson)
            }
            array.put(obj)
        }
        return array.toString(2)
    }

    suspend fun importTasksFromJson(jsonString: String): Int {
        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<TaskItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val task = TaskItem(
                    id = 0, // auto-generate new IDs on import to prevent collision
                    title = obj.optString("title", "Imported Task"),
                    notes = obj.optString("notes", ""),
                    isCompleted = obj.optBoolean("isCompleted", false),
                    completedAt = if (obj.has("completedAt") && !obj.isNull("completedAt")) obj.getLong("completedAt") else null,
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    dueDate = obj.optString("dueDate", ""),
                    dueTime = obj.optString("dueTime", ""),
                    dueTimestamp = if (obj.has("dueTimestamp") && !obj.isNull("dueTimestamp")) obj.getLong("dueTimestamp") else null,
                    priority = obj.optString("priority", "MEDIUM"),
                    category = obj.optString("category", "Personal"),
                    hasReminder = obj.optBoolean("hasReminder", false),
                    reminderTimestamp = if (obj.has("reminderTimestamp") && !obj.isNull("reminderTimestamp")) obj.getLong("reminderTimestamp") else null,
                    recurrence = obj.optString("recurrence", "NONE"),
                    subtasksJson = obj.optString("subtasksJson", "[]")
                )
                list.add(task)
            }
            if (list.isNotEmpty()) {
                taskDao.insertTasks(list)
            }
            list.size
        } catch (_: Exception) {
            0
        }
    }
}
