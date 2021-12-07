package dolphin.android.apps.BloodServiceApp.ui

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.os.BuildCompat
import dolphin.android.apps.BloodServiceApp.AppDataModel
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.provider.SpotList
import dolphin.android.util.DebugOnlyNoCoverage

/**
 * Main theme of the app.
 *
 * @param content page content
 */
@SuppressLint("NewApi")
@Composable
fun AppTheme(
    dark: Boolean = isSystemInDarkTheme(),
    dynamic: Boolean = BuildCompat.isAtLeastS(),
    content: @Composable BoxScope.() -> Unit
) {
    val colorScheme = when {
        dynamic && dark -> dynamicDarkColorScheme(LocalContext.current)
        dynamic && !dark -> dynamicLightColorScheme(LocalContext.current)
        dark -> AppDarkThemeColors
        else -> AppLightThemeColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
    ) {
//        // https://stackoverflow.com/a/69000913
//        CompositionLocalProvider(
//            LocalIndication provides rememberRipple(color = colorScheme.tertiary),
//        ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            content = content,
            contentAlignment = Alignment.Center, // default alignment
        )
//        }
    }
}

/**
 * Common separator.
 *
 * @param modifier [Modifier] to apply to this layout node.
 * @param horizontalPadding horizontal padding
 * @param verticalPadding vertical padding
 * @param color separator line color
 */
@Composable
fun Separator(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 8.dp,
    verticalPadding: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = .25f),
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .background(color)
            .requiredHeight(1.dp)
            .padding(vertical = verticalPadding)
    )
}

/**
 * App UI callbacks
 */
interface AppUiCallback :
    WelcomeUiCallback,
    MainUiCallback,
    SpotListUiCallback,
    SettingsUiCallback {

    /**
     * Callback when user press BACK button on UI.
     */
    fun pressBack()

    /**
     * Review data source in a browser
     *
     * @param center target blood center
     */
    fun reviewSource(center: BloodCenter.Center)
}

enum class UiState {
    Welcome, Main, Settings, Spots
}

/**
 * App UI in Compose way.
 *
 * @param model data model that contains all essential data
 * @param center a [BloodCenter] instance that loads all translations
 * @param callback a interface to interact with the main controller (usually a Activity or Fragment)
 * @param modifier [Modifier] to apply to this layout node.
 */
@SuppressLint("NewApi")
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun AppUiPane(
    model: AppDataModel,
    center: BloodCenter,
    callback: AppUiCallback,
    modifier: Modifier = Modifier,
) {
    AppTheme(dark = model.darkMode.collectAsState().value) {
        val selected = model.center.observeAsState()
        val maps = model.storageMap.collectAsState()
        val days = model.daysList.collectAsState()
        val cities = model.spotList.collectAsState()
        val city = model.city.collectAsState()
        val review = model.showPrivacyReview.collectAsState()
        val ads = model.showAds.collectAsState()

        Crossfade(
            targetState = model.uiState.observeAsState().value,
            modifier = modifier
        ) { state ->
            when (state) {
                UiState.Welcome ->
                    WelcomeUi(
                        list = center.values(),
                        onComplete = { index -> callback.reviewComplete(center.values()[index]) },
                        modifier = Modifier.fillMaxSize(),
                        onReview = { callback.reviewPrivacy() },
                        onSource = { callback.reviewSource(center.main()) },
                    )

                UiState.Main ->
                    MainUi(
                        centers = center.values(),
                        selected = selected.value ?: center.main(),
                        modifier = Modifier.fillMaxSize(),
                        daysList = days.value,
                        storageMap = maps.value,
                        onCenterChange = { c -> callback.changeBloodCenter(c) },
                        onAddCalendar = { event -> callback.addToCalendar(event) },
                        enableAddCalendar = callback.enableAddToCalendar(),
                        onSearchOnMap = { event -> callback.searchOnMaps(event) },
                        enableSearchOnMap = callback.enableSearchOnMap(),
                        onSpotListClick = { c -> callback.showSpotList(c) },
                        onDonorClick = { callback.showDonorInfo() },
                        onSettingsClick = { model.changeUiState(UiState.Settings) },
                        onReviewSource = { c -> callback.reviewSource(c) },
                        onFacebookClick = { c -> callback.showFacebookPages(c) },
                        onReviewIgnore = {
                            callback.reviewComplete(selected.value ?: center.main())
                        },
                        onReviewPolicy = { callback.reviewPrivacy() },
                        showReviewPolicy = review.value,
                    )

                UiState.Spots ->
                    SpotListUi(
                        list = cities.value,
                        modifier = Modifier.fillMaxSize(),
                        onBackPress = { callback.pressBack() },
                        onSpotClick = { info -> callback.showSpotInfo(info) },
                        onCityClick = { c -> model.changeCity(c.cityId) },
                        selectedCity = city.value,
                        showAds = ads.value,
                    )

                UiState.Settings ->
                    SettingsUi(
                        modifier = Modifier.fillMaxSize(),
                        onBackPress = { callback.pressBack() },
                        version = callback.versionInfo(),
                        onReview = { title, asset -> callback.showAssetInDialog(title, asset) },
                        showChangeLog = callback.enableVersionSummary(),
                        showAds = ads.value,
                        onToggleAds = { checked -> callback.toggleAds(checked) }
                    )

                else ->
                    LoadingAppUi(Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * A data collection for compose preview and debug.
 */
@DebugOnlyNoCoverage
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
        BloodCenter.Center(
            name = "Hsinchu Center",
            id = 2,
            cityIds = "16,17,15,18",
            cities = "Northern Taiwan other areas",
        ),
        BloodCenter.Center(
            name = "Taichung Center",
            id = 2,
            cityIds = "19,21,22,33",
            cities = "Central Taiwan",
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
        DonateDay(activities = listOf()), // put an empty list for test
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
