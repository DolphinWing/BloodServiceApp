package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.provider.SpotList

@Composable
fun AppTheme(content: @Composable BoxScope.() -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = colorResource(id = R.color.bloody_color),
            primaryVariant = colorResource(id = R.color.bloody_darker_color),
            secondary = colorResource(id = R.color.material_light_green_700),
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            content = content,
            contentAlignment = Alignment.Center, // default alignment
        )
    }
}

@Composable
fun Separator(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .background(Color.LightGray.copy(alpha = .5f))
            .requiredHeight(1.dp)
            .padding(vertical = 4.dp)
    )
}

object PreviewSample {
    val selectedCenter = BloodCenter.Center(
        name = "Tainan Center",
        id = 5,
        cityIds = "26,23,24",
        cities = "Tainan and Chiayi",
    )

    val centers = listOf(
        BloodCenter.Center(
            name = "Taipei Center",
            id = 2,
            cityIds = "13,14,12,32,31",
            cities = "Northern Taiwan",
        ),
        selectedCenter,
        BloodCenter.Center(
            name = "Kao Center",
            id = 6,
            cityIds = "27,29,34,30",
            cities = "Southern Taiwan",
        )
    )

    fun storage(a: Int = 3, b: Int = 3, o: Int = 3, ab: Int = 3): HashMap<String, Int> =
        HashMap<String, Int>().apply {
            put("A", a)
            put("B", b)
            put("O", o)
            put("AB", ab)
        }

    val donations = listOf(
        DonateDay(
            activities = listOf(
                DonateActivity("Protective Service Occupations", "3535 Clousson Road"),
                DonateActivity("Foreman & Clark", "Sacramento, California"),
                DonateActivity("Climer", "Lake Worth, FL"),
                DonateActivity("Higginbotham Rd", "Georgetown, South Carolina"),
            )
        ),
        DonateDay(
            activities = listOf(
                DonateActivity("De-Jaiz Mens Clothing", "439 Wines Lane, Houston"),
                DonateActivity("High school", "377 Saint James Drive, Harrisburg, Pennsylvania"),
            )
        ),
        DonateDay(
            activities = listOf(
                DonateActivity("Cleveland", "715 Lakeland Park Drive"),
                DonateActivity("La Luz, New Mexico", "745 Reynolds StGreen River, Wyoming"),
            )
        ),
        DonateDay(
            activities = listOf(
                DonateActivity("Middle Ground Church", "SW Garden LnTopeka, Kansas"),
                DonateActivity("Johnson Lake", "San Gabriel, California"),
                DonateActivity("Lynn School", "Bluejacket, Oklahoma"),
                DonateActivity("Patriots", "404 Quartz Rd, Superior, Montana"),
            )
        )
    )

    val spots = listOf(
        SpotList(10).apply {
            cityName = "Sweetie"
            addStaticLocation(SpotInfo(1, 10, "Here some place"))
            addStaticLocation(SpotInfo(2, 10, "One more place"))
            addStaticLocation(SpotInfo(3, 10, "Last place"))
            addDynamicLocation(SpotInfo(11, 10, "Somewhere"))
            addDynamicLocation(SpotInfo(12, 10, "Nowhere"))
        },
        SpotList(20).apply {
            cityName = "Moon"
            addStaticLocation(SpotInfo(1, 10, "Here and there"))
            addDynamicLocation(SpotInfo(2, 10, "Far far away"))
            addDynamicLocation(SpotInfo(3, 10, "Far far far away"))
        },
    )
}