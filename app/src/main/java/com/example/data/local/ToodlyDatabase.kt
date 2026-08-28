package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.TaskItem
import com.example.data.model.TemplateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskItem::class, CategoryEntity::class, TemplateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ToodlyDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: ToodlyDatabase? = null

        fun getInstance(context: Context): ToodlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToodlyDatabase::class.java,
                    "toodly_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateInitialData(getInstance(context))
            }
        }

        private suspend fun populateInitialData(database: ToodlyDatabase) {
            // Default Categories
            val defaultCategories = listOf(
                CategoryEntity(id = 1, name = "Personal", iconName = "person", colorHex = "#7C68EE"),
                CategoryEntity(id = 2, name = "Work", iconName = "work", colorHex = "#5E9BFF"),
                CategoryEntity(id = 3, name = "Study", iconName = "school", colorHex = "#FF7698"),
                CategoryEntity(id = 4, name = "Fitness", iconName = "fitness", colorHex = "#27AE60"),
                CategoryEntity(id = 5, name = "Home", iconName = "home", colorHex = "#F2994A"),
                CategoryEntity(id = 6, name = "Groceries", iconName = "shopping_cart", colorHex = "#9B51E0")
            )
            database.categoryDao().insertCategories(defaultCategories)

            // Default Productivity Templates as specified in PRD
            val defaultTemplates = listOf(
                TemplateEntity(
                    id = 1,
                    name = "Work Focus",
                    description = "Stay productive and organized at work",
                    category = "Work",
                    iconName = "target",
                    colorHex = "#7C68EE",
                    estimatedTimeMinutes = 30,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Set today's 3 major goals",
                            "Check and clear priority emails",
                            "Focus on deep-work main project (90 min)",
                            "Take a 10-minute movement break",
                            "Review progress and prepare tomorrow's plan"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 2,
                    name = "Study Plan",
                    description = "Organize your learning and exam prep",
                    category = "Study",
                    iconName = "book",
                    colorHex = "#FF7698",
                    estimatedTimeMinutes = 45,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Review previous chapter summary",
                            "Read new topic module (25 min)",
                            "Write active recall flashcards",
                            "Solve 5 practice problems",
                            "Summarize key takeaways in notebook"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 3,
                    name = "Morning Routine",
                    description = "Start your day centered and energized",
                    category = "Personal",
                    iconName = "sun",
                    colorHex = "#F2994A",
                    estimatedTimeMinutes = 20,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Drink 500ml water with lemon",
                            "5 minutes light stretching or yoga",
                            "Healthy breakfast & vitamins",
                            "Review today's schedule on Toodly",
                            "5 minutes mindfulness meditation"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 4,
                    name = "Fitness Routine",
                    description = "Build a healthier, stronger you",
                    category = "Fitness",
                    iconName = "dumbbell",
                    colorHex = "#27AE60",
                    estimatedTimeMinutes = 45,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Dynamic warm-up & joint mobility (5 min)",
                            "Core workout session (30 min)",
                            "Post-workout stretch & cool down",
                            "Log reps & hydration in tracker",
                            "Protein shake & nutritious meal"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 5,
                    name = "Grocery List",
                    description = "Essential weekly market run",
                    category = "Groceries",
                    iconName = "cart",
                    colorHex = "#9B51E0",
                    estimatedTimeMinutes = 25,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Fresh vegetables (spinach, tomatoes, carrots)",
                            "Fruits (bananas, berries, apples)",
                            "Oat milk & Greek yogurt",
                            "Whole grain bread & eggs",
                            "Olive oil & pantry essentials"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 6,
                    name = "Travel Checklist",
                    description = "Stress-free packing and departure prep",
                    category = "Personal",
                    iconName = "flight",
                    colorHex = "#5E9BFF",
                    estimatedTimeMinutes = 35,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Passports, ID, and travel tickets",
                            "Phone charger, power bank & adapters",
                            "Weather-appropriate clothes & footwear",
                            "Toiletry kit & prescription medications",
                            "Lock windows, turn off appliances, set alarms"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 7,
                    name = "Cleaning & Tidying",
                    description = "Keep your living space fresh and clean",
                    category = "Home",
                    iconName = "sparkles",
                    colorHex = "#FFB347",
                    estimatedTimeMinutes = 30,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Wipe down kitchen counters & table",
                            "Load dishwasher & wash dishes",
                            "Vacuum living room and bedroom floor",
                            "Take out recycling & trash bins",
                            "Air out rooms for 15 minutes"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 8,
                    name = "Personal Goals",
                    description = "Weekly reflection and long-term milestones",
                    category = "Personal",
                    iconName = "star",
                    colorHex = "#7C68EE",
                    estimatedTimeMinutes = 20,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Write down 3 wins from this week",
                            "Review progress on monthly savings target",
                            "Read 20 pages of current non-fiction book",
                            "Reach out to an old friend or mentor",
                            "Set intention for the coming week"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 9,
                    name = "Content Creation",
                    description = "Plan, script, and publish your work",
                    category = "Work",
                    iconName = "camera",
                    colorHex = "#FF5E7E",
                    estimatedTimeMinutes = 60,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Brainstorm 5 content hooks & titles",
                            "Draft outline and key speaking points",
                            "Record video / write full copy draft",
                            "Design thumbnail or cover image",
                            "Schedule post & engage with comments"
                        )
                    ),
                    isCustom = false
                ),
                TemplateEntity(
                    id = 10,
                    name = "Daily Reset",
                    description = "Evening wind-down for peaceful sleep",
                    category = "Personal",
                    iconName = "moon",
                    colorHex = "#6C5CE7",
                    estimatedTimeMinutes = 15,
                    tasksJson = TemplateEntity.serializeTaskList(
                        listOf(
                            "Clear desk and put away clutter",
                            "Check off completed tasks in Toodly",
                            "Pick tomorrow's top priority task",
                            "Dim lights and put phone on Do Not Disturb",
                            "10 minutes relaxing reading"
                        )
                    ),
                    isCustom = false
                )
            )
            database.templateDao().insertTemplates(defaultTemplates)
        }
    }
}
