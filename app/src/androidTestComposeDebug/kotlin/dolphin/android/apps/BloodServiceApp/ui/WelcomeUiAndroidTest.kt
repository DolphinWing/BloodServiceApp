package dolphin.android.apps.BloodServiceApp.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.tests.assertTextDisplayed
import dolphin.android.tests.tag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterial3Api
class WelcomeUiAndroidTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var center: BloodCenter

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        center = BloodCenter(context)
    }

    private fun getString(@StringRes id: Int): String = context.getString(id)

    @Test
    fun welcome() {
        composeTestRule.setContent {
            WelcomeUi(list = center.values(), onComplete = {})
        }
        composeTestRule.assertTextDisplayed(getString(R.string.app_name))
    }

    @Test
    fun loading() {
        composeTestRule.setContent {
            LoadingAppUi()
        }
        composeTestRule.tag("loading").assertIsDisplayed()
    }
}