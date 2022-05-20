package dolphin.android.apps.BloodServiceApp

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.BloodDataParser
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.PrefsDataStore
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.ui.AppUiCallback
import dolphin.android.apps.BloodServiceApp.ui.AppUiPane
import dolphin.android.apps.BloodServiceApp.ui.UiState
import dolphin.android.util.NoCoverageRequired
import dolphin.android.util.PackageUtils
import dolphin.android.util.readFromAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
class MainActivity : AppCompatActivity(), AppUiCallback {
    companion object {
        private const val TAG = "MainUi"
    }

    private val model: AppDataModel by viewModels()
    private lateinit var centerInstance: BloodCenter
    private lateinit var dataStore: PrefsDataStore
    private lateinit var parser: BloodDataParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        centerInstance = BloodCenter(this)
        dataStore = PrefsDataStore(this)
        parser = BloodDataParser(this)

        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupPreDrawListener()
        }

        setupViewModel()

        setContent {
            val dark = isSystemInDarkTheme() // try to detect system theme
            LaunchedEffect(Unit) {
                changeToDarkMode(dark)
            }

            val windowSize = calculateWindowSizeClass(activity = this)
            Log.v(TAG, "size = ${windowSize.widthSizeClass}")

            AppUiPane(
                model = model,
                center = centerInstance,
                callback = this,
                modifier = Modifier.fillMaxSize(),
            )
        }

        setupFirebaseRemoteConfig()
        setupMobileAds()
    }

    private fun setupPreDrawListener() {
        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            @NoCoverageRequired object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (model.ready.value == true) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )
    }

    private fun setupFirebaseRemoteConfig() {
        // Handler(Looper.getMainLooper()).postDelayed({ checkPrivacyPolicyReview() }, 5)
        Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            checkPrivacyPolicyReview()
            // executor.submit { helper.warmup() }
            model.ready.postValue(true)
        }
    }

    private fun setupMobileAds() {
        MobileAds.initialize(this) {
        }
    }

    private fun setupViewModel() {
        background {
            dataStore.centerId.map { centerId ->
                centerInstance.find(centerId)
            }.collect { bloodCenter ->
                whenBloodCenterChanged(bloodCenter)
            }
        }
        background {
            dataStore.mobileAds.collect { enabled ->
                model.showAds.emit(enabled)
            }
        }
        background {
            model.init(parser).collect { ready ->
                // Log.d(TAG, "storage ready $ready")
                if (ready) {
                    queryStorageData(model.center.value?.id ?: -1, forceRefresh = true)
                }
            }
        }
        model.uiState.observe(this) { state ->
            setScreenName(state.name)
        }
    }

    private fun checkPrivacyPolicyReview() {
        val firebaseCode = Firebase.remoteConfig.getLong("privacy_policy_update_code")
        Log.v(TAG, "firebase code = $firebaseCode")
        background {
            dataStore.policyCode.collect { appCode ->
                Log.v(TAG, "app code = $appCode")
                model.showPrivacyReview.emit(appCode < firebaseCode)
            }
        }
    }

    override fun onBackPressed() {
        when (model.state) {
            UiState.Spots, UiState.Settings -> model.changeUiState(UiState.Main)
            else -> super.onBackPressed()
        }
    }

    override fun pressBack() {
        onBackPressed()
    }

    override fun reviewSource(center: BloodCenter.Center) {
        if (center.id == 0) {
            logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
                putString(FirebaseAnalytics.Param.ITEM_ID, centerInstance.main().name)
            }
            IntentHelper.showMainSource(this)
        } else {
            logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
                putString(FirebaseAnalytics.Param.ITEM_ID, center.name)
            }
            IntentHelper.showBloodCenterSource(this, center.id)
        }
    }

    override fun reviewPrivacy() {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "review-privacy")
        }
        showAssetInDialog(
            title = R.string.app_privacy_policy,
            asset = "privacy_tw.md",
        )
    }

    override fun reviewComplete(center: BloodCenter.Center) {
        val code = Firebase.remoteConfig.getLong("privacy_policy_update_code")
        Log.v(TAG, "review complete ($code)")
        background { dataStore.updatePolicyCode(code) }
        changeBloodCenter(center)
        if (model.state == UiState.Welcome) {
            model.changeUiState(UiState.Main)
        }
    }

    override fun changeBloodCenter(center: BloodCenter.Center) {
        background { dataStore.changeCenter(center.id) }
    }

    private fun whenBloodCenterChanged(center: BloodCenter.Center) {
        if (model.state == UiState.Settings) return // not gonna change here
        Log.v(TAG, "change to ${center.name}")
        if (center.id > 0) {
            model.center.postValue(center)
            setUserProperty("center", center.name)
            queryDonationData(center.id)
            queryStorageData(center.id)
            model.changeUiState(UiState.Main)
        } else {
            model.changeUiState(UiState.Welcome)
        }
    }

    override fun showFacebookPages(center: BloodCenter.Center) {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
            putString(FirebaseAnalytics.Param.ITEM_ID, "show-facebook")
        }
        IntentHelper.showBloodCenterFacebookPages(this, center.id)
    }

    private var queryDonationJob: Job? = null
    private var queryStorageJob: Job? = null
    private var querySpotListJob: Job? = null

    private fun queryDonationData(id: Int) {
        queryDonationJob?.cancel()
        queryDonationJob = background {
            model.getDonationData(parser, id).collect { loading ->
                Log.d(TAG, "  queryDonationData $id loading = $loading")
            }
        }
    }

    private fun queryStorageData(id: Int, forceRefresh: Boolean = false) {
        queryStorageJob?.cancel()
        queryStorageJob = background {
            model.getStorageData(parser, forceRefresh, centerId = id).collect { loading ->
                Log.d(TAG, "  queryStorageData $id loading = $loading")
            }
        }
    }

    private fun querySpotList(id: Int) {
        querySpotListJob?.cancel()
        querySpotListJob = background {
            model.getSpotListData(parser, id).collect { loading ->
                Log.d(TAG, "  querySpotList $id loading = $loading")
            }
        }
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "donation-spot-list")
        }
    }

    override fun showSpotList(center: BloodCenter.Center) {
        querySpotList(center.id)
        model.changeUiState(UiState.Spots)
    }

    override fun showSpotInfo(info: SpotInfo) {
        IntentHelper.showSpotInfo(this, info)
    }

    override fun addToCalendar(event: DonateActivity) {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "add-to-calendar")
        }
        IntentHelper.addToCalendar(this, event)
    }

    override fun enableAddToCalendar(): Boolean = true

    override fun searchOnMaps(event: DonateActivity) {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "search-on-map")
        }
        IntentHelper.searchOnMap(this, event)
    }

    override fun enableSearchOnMap(): Boolean {
        return Firebase.remoteConfig.getBoolean("enable_search_on_map")
    }

    override fun showDonorInfo() {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
            putString(FirebaseAnalytics.Param.ITEM_ID, "show-donor-info")
        }
        IntentHelper.showDonorInfo(this)
    }

    override fun showAssetInDialog(title: Int, asset: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(readFromAssets(asset, "UTF-8"))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                reviewComplete(model.center.value ?: centerInstance.main())
                dialog.dismiss()
            }
            .setNeutralButton(R.string.action_more_info) { _, _ ->
                IntentHelper.showGithubPages(this)
                reviewComplete(model.center.value ?: centerInstance.main()) // mark complete
            }
            .setCancelable(true)
            .show().apply {
                findViewById<TextView>(android.R.id.message)?.textSize = 12f
            }
    }

    override fun versionInfo(): String {
        return PackageUtils.getPackageInfo(this, this::class.java)?.let { info ->
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("deprecation") info.versionCode
            }
            "v${info.versionName} (r$code)"
        } ?: kotlin.run { "" }
    }

    override fun enableVersionSummary(): Boolean {
        return BuildConfig.DEBUG || Firebase.remoteConfig.getBoolean("enable_change_log_summary")
    }

    override fun toggleAds(checked: Boolean) {
        Log.d(TAG, "toggle ads to $checked")
        background { dataStore.toggleAds(checked) }
    }

    private fun logEvent(name: String, block: (Bundle.() -> Unit)? = null) {
        val data = if (block != null) Bundle().apply(block) else null
        FirebaseAnalytics.getInstance(this).logEvent(name, data)
    }

    private fun setScreenName(screenName: String?, screenClassOverride: String? = null) {
        // See https://firebase.googleblog.com/2020/08/google-analytics-manual-screen-view.html
        // firebaseAnalytics.setCurrentScreen(activity, screenName, screenClassOverride)
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) @NoCoverageRequired {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClassOverride?.let { s -> putString(FirebaseAnalytics.Param.SCREEN_CLASS, s) }
        }
    }

    @Suppress("SameParameterValue")
    private fun setUserProperty(key: String, value: String?) {
        FirebaseAnalytics.getInstance(this).setUserProperty(key, value)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // dynamic changed with the system
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                changeToDarkMode(false)
                Log.d(TAG, "Night mode is not active, we're using the light theme")
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                changeToDarkMode(true)
                Log.d(TAG, "Night mode is active, we're using dark theme")
            }
        }
    }

    private fun background(block: suspend CoroutineScope.() -> Unit) =
        lifecycleScope.launch(block = block)

    private fun changeToDarkMode(dark: Boolean = true) {
        background { model.darkMode.emit(dark) }
    }
}
