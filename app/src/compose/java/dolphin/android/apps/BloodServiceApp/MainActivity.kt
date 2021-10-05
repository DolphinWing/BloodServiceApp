package dolphin.android.apps.BloodServiceApp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.ui.AppUiCallback
import dolphin.android.apps.BloodServiceApp.ui.AppUiPane
import dolphin.android.apps.BloodServiceApp.ui.UiState
import dolphin.android.util.PackageUtils

@ExperimentalFoundationApi
class MainActivity : AppCompatActivity(), AppUiCallback {
    companion object {
        private const val TAG = "MainUi"
    }

    private val model: AppDataModel by viewModels()
    private lateinit var centerInstance: BloodCenter
    private lateinit var prefs: PrefsUtil
    private lateinit var helper: BloodDataHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        centerInstance = BloodCenter(this)
        prefs = PrefsUtil(this)
        helper = BloodDataHelper(this)

        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupPreDrawListener()
        }

        setupViewModel()

        setContent {
            AppUiPane(
                model = model,
                center = centerInstance,
                callback = this,
                modifier = Modifier.fillMaxSize(),
            )
        }

        setupFirebaseRemoteConfig()
    }

    private fun setupPreDrawListener() {
        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
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
        })
    }

    private fun setupFirebaseRemoteConfig() {
        // Handler(Looper.getMainLooper()).postDelayed({ checkPrivacyPolicyReview() }, 5)
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            checkPrivacyPolicyReview()
            // executor.submit { helper.warmup() }
            model.ready.postValue(true)
        }
    }

    private fun setupViewModel() {
        model.init(helper).observe(this) { ready ->
            // Log.d(TAG, "storage ready $ready")
            if (ready) model.getStorageData(helper, true).observe(this) { cache ->
                // Log.d(TAG, ">> array: ${cache.size()}")
                model.center.value?.let { center ->
                    try {
                        model.updateStorageMap(center.id, cache[center.id])
                    } catch (e: Exception) {
                        // pre-30 app can't use cache.contains(center.id)
                    }
                }
            }
        }
        model.center.observe(this) { bloodCenter ->
            Log.v(TAG, "change to ${bloodCenter.name}")
            setUserProperty("center", bloodCenter.name)
            queryDonationData(bloodCenter.id)
            queryStorageData(bloodCenter.id)
        }
        model.center.postValue(centerInstance.find(prefs.centerId))
        model.uiState.observe(this) { state ->
            setScreenName(state.name)
        }
    }

    private fun checkPrivacyPolicyReview() {
        val prefs = PrefsUtil(this)
        val firebaseCode = Firebase.remoteConfig.getLong("privacy_policy_update_code")
        Log.v(TAG, "firebase code = $firebaseCode")
        if (prefs.centerId > 0) {
            model.changeUiState(UiState.Main)
        } else {
            model.changeUiState(UiState.Welcome)
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
            logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
                putString(FirebaseAnalytics.Param.ITEM_ID, centerInstance.main().name)
            }
            IntentHelper.showMainSource(this)
        } else {
            logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
                putString(FirebaseAnalytics.Param.ITEM_ID, center.name)
            }
            IntentHelper.showBloodCenterSource(this, center.id)
        }
    }

    override fun reviewPrivacy() {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "review-privacy")
        }
        showAssetInDialog(
            title = R.string.app_privacy_policy,
            asset = "privacy_policy.txt",
        )
    }

    override fun reviewComplete(center: BloodCenter.Center) {
        // Toast.makeText(this, "accept", Toast.LENGTH_SHORT).show()
        prefs.policyCode = Firebase.remoteConfig.getLong("privacy_policy_update_code")
        changeBloodCenter(center)
        model.changeUiState(UiState.Main)
    }

    override fun changeBloodCenter(center: BloodCenter.Center) {
        prefs.centerId = center.id
        model.center.postValue(center)
    }

    override fun showFacebookPages(center: BloodCenter.Center) {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
            putString(FirebaseAnalytics.Param.ITEM_ID, "show-facebook")
        }
        IntentHelper.showBloodCenterFacebookPages(this, center.id)
    }

    private fun queryDonationData(id: Int) {
        model.loading(true) // download donation events
        model.getDonationData(helper, id).observe(this) { days ->
            model.updateEventList(id, days)
            model.loading(false) // donation events downloaded
        }
    }

    private fun queryStorageData(id: Int) {
        model.updateStorageMap(id, model.getStorageData(id))
    }

    private fun querySpotList(id: Int) {
        model.loading(true) // download spot list
        model.updateSpotList(id, ArrayList()) // empty the list first
        model.getSpotList(helper, id).observe(this) { spotList ->
            model.updateSpotList(id, spotList)
            model.loading(false) // spot list downloaded
        }
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
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
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "add-to-calendar")
        }
        IntentHelper.addToCalendar(this, event)
    }

    override fun enableAddToCalendar(): Boolean = true

    override fun searchOnMaps(event: DonateActivity) {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "action")
            putString(FirebaseAnalytics.Param.ITEM_ID, "search-on-map")
        }
        IntentHelper.searchOnMap(this, event)
    }

    override fun enableSearchOnMap(): Boolean {
        return Firebase.remoteConfig.getBoolean("enable_search_on_map")
    }

    override fun showDonorInfo() {
        logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "browser")
            putString(FirebaseAnalytics.Param.ITEM_ID, "show-donor-info")
        }
        IntentHelper.showDonorInfo(this)
    }

    override fun showAssetInDialog(title: Int, asset: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(PrefsUtil.read_asset_text(this, asset, "UTF-8"))
            .setPositiveButton(android.R.string.ok, null)
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
                info.versionCode
            }
            "v${info.versionName} (r$code)"
        } ?: kotlin.run { "" }
    }

    override fun enableVersionSummary(): Boolean {
        return BuildConfig.DEBUG || Firebase.remoteConfig.getBoolean("enable_change_log_summary")
    }

    private fun logEvent(name: String, block: (Bundle.() -> Unit)? = null) {
        val data = if (block != null) Bundle().apply(block) else null
        FirebaseAnalytics.getInstance(this).logEvent(name, data)
    }

    private fun setScreenName(screenName: String?, screenClassOverride: String? = null) {
        // See https://firebase.googleblog.com/2020/08/google-analytics-manual-screen-view.html
        // firebaseAnalytics.setCurrentScreen(activity, screenName, screenClassOverride)
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClassOverride?.let { s -> putString(FirebaseAnalytics.Param.SCREEN_CLASS, s) }
        }
    }

    @Suppress("SameParameterValue")
    private fun setUserProperty(key: String, value: String?) {
        FirebaseAnalytics.getInstance(this).setUserProperty(key, value)
    }
}