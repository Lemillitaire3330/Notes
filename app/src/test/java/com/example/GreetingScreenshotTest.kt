package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.SecureNote
import com.example.ui.components.NoteCard
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
    val sampleNote = SecureNote(
      id = 1,
      title = "Mes Mots de Passe Secrets",
      content = "Contenu secret",
      password = "1234",
      passwordHint = "PIN classique",
      colorIndex = 0,
      isPinned = true,
      isAutoSaveEnabled = true,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        NoteCard(
          note = sampleNote,
          isUnlocked = false,
          onClick = {},
          onTogglePin = {},
          onDelete = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
