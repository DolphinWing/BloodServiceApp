package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createComposeRule
import dolphin.android.tests.assertTextDisplayed
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterial3Api
class SpotListUiAndroidTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun spotList() {
        composeTestRule.setContent {
            SpotListUi(list = PreviewSample.spots)
        }
        PreviewSample.spots.forEach { city ->
            city.cityName?.let { name -> composeTestRule.assertTextDisplayed(name) }
        }
    }
}