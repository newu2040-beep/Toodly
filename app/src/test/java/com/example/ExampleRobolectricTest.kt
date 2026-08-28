package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.DayOverview
import com.example.viewmodel.ToodlyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Toodly", appName)
  }

  @Test
  fun `day overview calculates completion rate correctly`() {
    val day = DayOverview(
      dayAbbreviation = "Mon",
      dayLetter = "M",
      dateString = "2026-08-24",
      dayOfMonth = 24,
      completedCount = 3,
      totalCount = 4,
      isToday = false
    )
    assertEquals(0.75f, day.completionRate, 0.001f)
  }

  @Test
  fun `getWeekDates returns 7 consecutive days`() {
    val week = ToodlyViewModel.getWeekDates("Monday")
    assertEquals(7, week.size)
  }
}
