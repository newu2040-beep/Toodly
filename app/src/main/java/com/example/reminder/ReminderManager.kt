package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.ToodlyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReminderManager(private val context: Context) {

    companion object {
        const val CHANNEL_TASK_REMINDERS = "channel_task_reminders"
        const val CHANNEL_DAILY_PLANNING = "channel_daily_planning"

        const val ACTION_TASK_REMINDER = "com.example.toodly.ACTION_TASK_REMINDER"
        const val ACTION_DAILY_PLANNING = "com.example.toodly.ACTION_DAILY_PLANNING"
        const val ACTION_MARK_DONE = "com.example.toodly.ACTION_MARK_DONE"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_CATEGORY = "extra_task_category"

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val taskChannel = NotificationChannel(
                    CHANNEL_TASK_REMINDERS,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Timely reminders for scheduled tasks and to-dos"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    setShowBadge(true)
                }

                val dailyChannel = NotificationChannel(
                    CHANNEL_DAILY_PLANNING,
                    "Daily Planning",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily morning and evening planning reminders"
                    enableVibration(true)
                    setShowBadge(true)
                }

                notificationManager.createNotificationChannel(taskChannel)
                notificationManager.createNotificationChannel(dailyChannel)
            }
        }

        fun parseDateAndTimeToMillis(dueDate: String, dueTime: String): Long? {
            if (dueDate.isBlank()) return null
            val timeToParse = if (dueTime.isBlank()) "09:00 AM" else dueTime.trim()
            val formats = listOf(
                "yyyy-MM-dd h:mm a",
                "yyyy-MM-dd hh:mm a",
                "yyyy-MM-dd H:mm",
                "yyyy-MM-dd HH:mm"
            )
            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.US)
                    sdf.isLenient = false
                    val date = sdf.parse("$dueDate $timeToParse")
                    if (date != null) {
                        return date.time
                    }
                } catch (_: Exception) {}
            }
            // Fallback: parse just date and set to 9 AM
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = sdf.parse(dueDate)
                if (date != null) {
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.set(Calendar.HOUR_OF_DAY, 9)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.timeInMillis
                } else null
            } catch (_: Exception) {
                null
            }
        }
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    init {
        createNotificationChannels(context)
    }

    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun sendImmediateTestNotification(title: String = "Toodly Reminder Alert ✨", message: String = "Don't forget to complete your pending tasks!") {
        createNotificationChannels(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            88888,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASK_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\nStay on track with your day and check off your to-dos! 🎯"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(88888, notification)
        } catch (_: SecurityException) {
            // Notifications disabled or permission missing
        }
    }

    fun scheduleTaskReminder(taskId: Long, title: String, category: String, triggerAtMillis: Long) {
        if (alarmManager == null || triggerAtMillis <= System.currentTimeMillis()) return

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TASK_TITLE, title)
            putExtra(EXTRA_TASK_CATEGORY, category)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } catch (_: Exception) {}
        }
    }

    fun cancelTaskReminder(taskId: Long) {
        if (alarmManager == null) return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun scheduleDailyPlanning(hour: Int, minute: Int) {
        if (alarmManager == null) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_DAILY_PLANNING
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            99999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {}
    }

    fun cancelDailyPlanning() {
        if (alarmManager == null) return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_DAILY_PLANNING
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            99999,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ReminderManager.ACTION_TASK_REMINDER -> {
                val taskId = intent.getLongExtra(ReminderManager.EXTRA_TASK_ID, 0L)
                val taskTitle = intent.getStringExtra(ReminderManager.EXTRA_TASK_TITLE) ?: "Task Reminder"
                val taskCategory = intent.getStringExtra(ReminderManager.EXTRA_TASK_CATEGORY) ?: "Personal"

                showTaskNotification(context, taskId, taskTitle, taskCategory)
            }
            ReminderManager.ACTION_DAILY_PLANNING -> {
                showDailyPlanningNotification(context)
            }
            ReminderManager.ACTION_MARK_DONE -> {
                val taskId = intent.getLongExtra(ReminderManager.EXTRA_TASK_ID, 0L)
                if (taskId != 0L) {
                    markTaskCompleted(context, taskId)
                    // Cancel notification
                    NotificationManagerCompat.from(context).cancel(taskId.toInt())
                }
            }
        }
    }

    private fun showTaskNotification(context: Context, taskId: Long, title: String, category: String) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markDoneIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ReminderManager.ACTION_MARK_DONE
            putExtra(ReminderManager.EXTRA_TASK_ID, taskId)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId + 10000).toInt(),
            markDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ReminderManager.CHANNEL_TASK_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏰ Reminder: $title")
            .setContentText("Due now • $category")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Time to complete: $title\nCategory: $category"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Mark Done", markDonePendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
        } catch (_: SecurityException) {
            // Notifications disabled or permission missing
        }
    }

    private fun showDailyPlanningNotification(context: Context) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            99999,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ReminderManager.CHANNEL_DAILY_PLANNING)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Plan your day with Toodly ✨")
            .setContentText("Check your tasks and set today's top priorities!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Good morning! Take a moment to review today's schedule and organize your tasks for maximum focus. 🎯"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(99999, notification)
        } catch (_: SecurityException) {
            // Notifications disabled or permission missing
        }
    }

    private fun markTaskCompleted(context: Context, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = ToodlyDatabase.getInstance(context)
            val task = db.taskDao().getTaskById(taskId)
            if (task != null) {
                db.taskDao().updateTask(
                    task.copy(
                        isCompleted = true,
                        completedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}

