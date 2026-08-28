package com.example

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.TaskItem
import com.example.ui.components.TaskCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleTask = TaskItem(
      id = 1,
      title = "Design Toodly theme",
      notes = "Pastel aesthetic",
      category = "Personal",
      priority = "HIGH",
      dueDate = "2026-08-28",
      dueTime = "10:00 AM",
      isCompleted = false
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        TaskCard(
          task = sampleTask,
          onClick = {},
          onToggleCompletion = {},
          onDelete = {},
          modifier = Modifier.padding(16.dp)
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
