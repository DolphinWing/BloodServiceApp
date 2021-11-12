package dolphin.android.apps.BloodServiceApp.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.tests.assertTextDisplayed
import dolphin.android.tests.findAll
import dolphin.android.tests.tag
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainUiAndroidTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun getString(@StringRes id: Int): String = context.getString(id)

    @Test
    fun mainDefault() {
        composeTestRule.setContent {
            MainUi(
                centers = PreviewSample.centers,
                selected = PreviewSample.selectedCenter,
                daysList = PreviewSample.donations,
                storageMap = PreviewSample.storage(1, 2, 3, 0),
            )
        }
        composeTestRule.waitForIdle()
        // composeTestRule.assertTextDisplayed(PreviewSample.selectedCenter.name)
        composeTestRule.assertTextDisplayed(getString(R.string.action_go_to_personal))
        composeTestRule.tag("bloodTypeA").assertIsDisplayed()
        composeTestRule.tag("bloodTypeB").assertIsDisplayed()
        composeTestRule.tag("bloodTypeO").assertIsDisplayed()
        composeTestRule.tag("bloodTypeAB").assertIsDisplayed()
    }

    @Test
    fun mainNoCalendar() {
        composeTestRule.setContent {
            MainUi(
                centers = PreviewSample.centers,
                selected = PreviewSample.selectedCenter,
                daysList = PreviewSample.donations,
                enableAddCalendar = false,
            )
        }
        composeTestRule.waitForIdle()
        // composeTestRule.assertTextDisplayed(PreviewSample.selectedCenter.name)
        Assert.assertTrue(composeTestRule.findAll("calendar").isEmpty())
    }

    @Test
    fun mainNoMaps() {
        composeTestRule.setContent {
            MainUi(
                centers = PreviewSample.centers,
                selected = PreviewSample.selectedCenter,
                daysList = PreviewSample.donations,
                enableSearchOnMap = false,
            )
        }
        composeTestRule.waitForIdle()
        // composeTestRule.assertTextDisplayed(PreviewSample.selectedCenter.name)
        Assert.assertTrue(composeTestRule.findAll("map").isEmpty())
    }

    @Test
    fun mainNoActions() {
        composeTestRule.setContent {
            MainUi(
                centers = PreviewSample.centers,
                selected = PreviewSample.selectedCenter,
                daysList = PreviewSample.donations,
                enableAddCalendar = false,
                enableSearchOnMap = false,
            )
        }
        composeTestRule.waitForIdle()
        // composeTestRule.assertTextDisplayed(PreviewSample.selectedCenter.name)
        Assert.assertTrue(composeTestRule.findAll("calendar").isEmpty())
        Assert.assertTrue(composeTestRule.findAll("map").isEmpty())
    }

    @Test
    fun mainPrivacy() {
        composeTestRule.setContent {
            MainUi(
                centers = PreviewSample.centers,
                selected = PreviewSample.selectedCenter,
                daysList = PreviewSample.donations,
                storageMap = PreviewSample.storage(0, 3, 2, 1),
                showReviewPolicy = true,
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.assertTextDisplayed(getString(R.string.snackbar_privacy_policy_updated))
        composeTestRule.assertTextDisplayed(getString(R.string.snackbar_privacy_policy_review))
        composeTestRule.assertTextDisplayed(getString(R.string.snackbar_privacy_policy_ignore))
    }
}