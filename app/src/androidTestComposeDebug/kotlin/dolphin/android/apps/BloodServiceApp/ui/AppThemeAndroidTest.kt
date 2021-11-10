package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.os.BuildCompat
import dolphin.android.tests.assertTextDisplayed
import org.junit.Rule
import org.junit.Test

class AppThemeAndroidTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun darkTheme() {
        composeTestRule.setContent {
            AppTheme(dark = true, dynamic = false) {
                Text("dark")
            }
        }
        composeTestRule.assertTextDisplayed("dark")
    }

    @Test
    fun lightTheme() {
        composeTestRule.setContent {
            AppTheme(dark = false, dynamic = false) {
                Text("light")
            }
        }
        composeTestRule.assertTextDisplayed("light")
    }

    @Test
    fun dynamicDarkTheme() {
        if (BuildCompat.isAtLeastS()) {
            composeTestRule.setContent {
                AppTheme(dark = true, dynamic = true) {
                    Text("dark")
                }
            }
            composeTestRule.assertTextDisplayed("dark")
        }
    }

    @Test
    fun dynamicLightTheme() {
        if (BuildCompat.isAtLeastS()) {
            composeTestRule.setContent {
                AppTheme(dark = false, dynamic = true) {
                    Text("light")
                }
            }
            composeTestRule.assertTextDisplayed("light")
        }
    }

    @Test
    fun separator() {
        composeTestRule.setContent {
            Separator(color = Color.Red)
        }
    }
}