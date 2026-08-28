package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

enum class Priority(val label: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    companion object {
        fun fromString(value: String): Priority = try {
            valueOf(value.uppercase())
        } catch (_: Exception) {
            MEDIUM
        }
    }
}

enum class Recurrence(val label: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    companion object {
        fun fromString(value: String): Recurrence = try {
            valueOf(value.uppercase())
        } catch (_: Exception) {
            NONE
        }
    }
}

data class Subtask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("title", title)
            put("isCompleted", isCompleted)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): Subtask {
            return Subtask(
                id = json.optString("id", java.util.UUID.randomUUID().toString()),
                title = json.optString("title", ""),
                isCompleted = json.optBoolean("isCompleted", false)
            )
        }

        fun parseList(jsonString: String): List<Subtask> {
            if (jsonString.isBlank() || jsonString == "[]") return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<Subtask>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }

        fun serializeList(list: List<Subtask>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: String = "", // Format: "yyyy-MM-dd" e.g., "2026-08-28"
    val dueTime: String = "", // Format: "hh:mm a" e.g., "09:00 AM"
    val dueTimestamp: Long? = null,
    val priority: String = Priority.MEDIUM.name,
    val category: String = "Personal",
    val hasReminder: Boolean = false,
    val reminderTimestamp: Long? = null,
    val recurrence: String = Recurrence.NONE.name,
    val subtasksJson: String = "[]",
    val sortOrder: Int = 0
) {
    fun getSubtasks(): List<Subtask> = Subtask.parseList(subtasksJson)
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: String,
    val iconName: String,
    val colorHex: String,
    val estimatedTimeMinutes: Int = 30,
    val tasksJson: String, // JSON array of task title strings
    val isCustom: Boolean = false
) {
    fun getTaskList(): List<String> {
        if (tasksJson.isBlank()) return emptyList()
        return try {
            val array = JSONArray(tasksJson)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        fun serializeTaskList(tasks: List<String>): String {
            val array = JSONArray()
            tasks.forEach { array.put(it) }
            return array.toString()
        }
    }
}
