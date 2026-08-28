package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "toodly_preferences")

class PreferencesRepository(private val context: Context) {
    companion object {
        val KEY_WELCOME_COMPLETED = booleanPreferencesKey("welcome_completed")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // SYSTEM, LIGHT, DARK
        val KEY_ACCENT_COLOR = stringPreferencesKey("accent_color") // Lavender, Mint, Pink, Peach, SkyBlue
        val KEY_COMPLETION_ANIMATION = stringPreferencesKey("completion_animation") // Bounce, Confetti, Strike
        val KEY_WEEK_START_DAY = stringPreferencesKey("week_start_day") // Monday, Sunday
        val KEY_DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val KEY_DAILY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time") // "08:00 AM"
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_COMPACT_MODE = booleanPreferencesKey("compact_mode")
    }

    val isWelcomeCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_WELCOME_COMPLETED] ?: false
    }

    val compactMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_COMPACT_MODE] ?: false
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "SYSTEM"
    }

    val accentColor: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCENT_COLOR] ?: "Lavender"
    }

    val completionAnimation: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_COMPLETION_ANIMATION] ?: "Bounce"
    }

    val weekStartDay: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_WEEK_START_DAY] ?: "Monday"
    }

    val dailyReminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAILY_REMINDER_ENABLED] ?: false
    }

    val dailyReminderTime: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAILY_REMINDER_TIME] ?: "08:00 AM"
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: "Friend"
    }

    suspend fun setWelcomeCompleted(completed: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_WELCOME_COMPLETED] = completed }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode }
    }

    suspend fun setAccentColor(color: String) {
        context.dataStore.edit { prefs -> prefs[KEY_ACCENT_COLOR] = color }
    }

    suspend fun setCompletionAnimation(animation: String) {
        context.dataStore.edit { prefs -> prefs[KEY_COMPLETION_ANIMATION] = animation }
    }

    suspend fun setWeekStartDay(day: String) {
        context.dataStore.edit { prefs -> prefs[KEY_WEEK_START_DAY] = day }
    }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_DAILY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setDailyReminderTime(time: String) {
        context.dataStore.edit { prefs -> prefs[KEY_DAILY_REMINDER_TIME] = time }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs -> prefs[KEY_USER_NAME] = name }
    }

    suspend fun setCompactMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_COMPACT_MODE] = enabled }
    }
}
