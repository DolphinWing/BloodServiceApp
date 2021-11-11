package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import dolphin.android.tests.assertTextDisplayed
import dolphin.android.tests.tag
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterial3Api
class SettingsUiAndroidTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settings() {
        composeTestRule.setContent {
            SettingsUi(version = "v1.2.3")
        }
        composeTestRule.assertTextDisplayed("v1.2.3")
    }

    @Test
    fun settingsShowChangeLog() {
        composeTestRule.setContent {
            SettingsUi(showChangeLog = true)
        }
        composeTestRule.assertTextDisplayed("v0.0.0")
        composeTestRule.tag("version").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settingsShowPrivacy() {
        composeTestRule.setContent {
            SettingsUi()
        }
        composeTestRule.assertTextDisplayed("v0.0.0")
        composeTestRule.tag("privacy").performClick()
        composeTestRule.waitForIdle()
    }
}