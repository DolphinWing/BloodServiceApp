package dolphin.android.apps.BloodServiceApp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivityAndroidTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun welcome() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithText(context.getString(R.string.app_name), useUnmergedTree = true)
//            .assertIsDisplayed()
        Assert.assertTrue(composeTestRule.activity.enableAddToCalendar())
    }
}