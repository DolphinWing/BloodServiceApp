package dolphin.android.apps.BloodServiceApp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.provider.SpotList
import dolphin.android.apps.BloodServiceApp.ui.AppTheme
import dolphin.android.apps.BloodServiceApp.ui.MainUi
import dolphin.android.apps.BloodServiceApp.ui.SettingsUi
import dolphin.android.apps.BloodServiceApp.ui.SpotUi
import dolphin.android.apps.BloodServiceApp.ui.UiState
import dolphin.android.apps.BloodServiceApp.ui.WelcomeUi
import dolphin.android.util.PackageUtils

@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainUi"
    }

    private val model: AppDataModel by viewModels()
    private lateinit var center: BloodCenter
    private lateinit var prefs: PrefsUtil
    private lateinit var helper: BloodDataHelper

    private val events = MutableLiveData<List<DonateDay>>()
    private val maps = MutableLiveData<HashMap<String, Int>>()
    private val places = MutableLiveData<List<SpotList>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        center = BloodCenter(this)
        prefs = PrefsUtil(this)
        helper = BloodDataHelper(this)

        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupPreDrawListener()
        }

        setupViewModel()

        setContent {
            val state = model.uiState.observeAsState()
            val bloodCenter = model.center.observeAsState()

            AppTheme {
                when (state.value) {
                    UiState.Welcome ->
                        WelcomeUi(
                            list = center.values(),
                            onComplete = { index ->
                                changeOnSiteCenter(center.values()[index])
                                onAcceptPrivacyPolicy()
                            },
                            modifier = Modifier.fillMaxSize(),
                        )

                    UiState.Main ->
                        MainUi(
                            centers = center.values(),
                            selected = bloodCenter.value ?: center.main(),
                            modifier = Modifier.fillMaxSize(),
                            donations = events.observeAsState().value ?: ArrayList(),
                            storage = maps.observeAsState().value ?: HashMap(),
                            onCenterChange = { bloodCenter ->
                                // Log.d(TAG, "change center ${bloodCenter.id}")
                                changeOnSiteCenter(bloodCenter)
                            },
                            onAddCalendar = { event -> addActivityToCalendar(event) },
                            onSearchOnMap = { event -> showSearchMapDialog(event) },
                            onStationsClick = { c -> showSpotList(c) },
                            onDonorClick = { showDonorInfo() },
                            onSettingsClick = { model.changeUiState(UiState.Settings) },
                        )

                    UiState.Spots ->
                        SpotUi(
                            selected = bloodCenter.value ?: center.main(),
                            list = places.observeAsState().value ?: ArrayList(),
                            modifier = Modifier.fillMaxSize(),
                            onBackPress = { onBackPressed() },
                            onSpotClick = { info -> showSpotInfo(info) },
                        )

                    UiState.Settings ->
                        SettingsUi(
                            modifier = Modifier.fillMaxSize(),
                            onBackPress = { onBackPressed() },
                            version = versionInfo(),
                        )

                    else -> Text("Hello, Compose ${state.value}")
                }
            }
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
            Log.d(TAG, "storage ready $ready")
            if (ready) model.getStorageData(helper, true).observe(this) { cache ->
                // Log.d(TAG, ">> array: ${cache.size()}")
                model.center.value?.let { center ->
                    maps.postValue(cache[center.id])
                }
            }
        }
        model.center.observe(this) { center ->
            Log.v(TAG, "change to ${center.name}")
            queryDonationData(center.id)
            queryStorageData(center.id)
        }
        model.center.postValue(center.find(prefs.centerId))

    }

    private fun checkPrivacyPolicyReview() {
        val prefs = PrefsUtil(this)
        val firebaseCode = Firebase.remoteConfig.getLong("privacy_policy_update_code")
        Log.v(TAG, "firebase code = $firebaseCode")
        if (prefs.centerId > 0) {
            model.changeUiState(UiState.Welcome)
        } else {
            model.changeUiState(UiState.Main)
        }
    }

    private fun onAcceptPrivacyPolicy() {
        // Toast.makeText(this, "accept", Toast.LENGTH_SHORT).show()
        // prefs.policyCode = Firebase.remoteConfig.getLong("privacy_policy_update_code")
        model.changeUiState(UiState.Main)
    }

    override fun onBackPressed() {
        when (model.state) {
            UiState.Spots, UiState.Settings -> model.changeUiState(UiState.Main)
            else -> super.onBackPressed()
        }
    }

    private fun changeOnSiteCenter(bloodCenter: BloodCenter.Center) {
        model.center.postValue(bloodCenter)
        prefs.centerId = bloodCenter.id
    }

    private fun queryDonationData(id: Int) {
        model.loading(true) // download donation events
        model.getDonationData(helper, id).observe(this) { days ->
//            Log.d(TAG, "list: ${days.size}")
//            days.forEach { day ->
//                Log.d(TAG, "  ${day.dateString} ${day.activityCount}")
//            }
            events.postValue(days)
            model.loading(false) // donation events downloaded
        }
    }

    private fun queryStorageData(id: Int) {
        val map = model.getStorageData(id)
//        Log.d(TAG, "  A: ${map["A"]}")
//        Log.d(TAG, "  B: ${map["B"]}")
//        Log.d(TAG, "  O: ${map["O"]}")
//        Log.d(TAG, " AB: ${map["AB"]}")
        maps.postValue(map)
    }

    private fun addActivityToCalendar(donation: DonateActivity) {
        IntentHelper.addToCalendar(this, donation)
    }

    private fun showSearchMapDialog(donation: DonateActivity) {
        IntentHelper.searchOnMap(this, donation)
    }

    private fun querySpotList(id: Int) {
        model.loading(true) // download spot list
        places.postValue(ArrayList()) // empty the list first
        model.getSpotList(helper, id).observe(this) { spotList ->
            Log.d(TAG, "list: ${spotList.size}")
            spotList.forEach { city ->
                Log.d(TAG, "  ${city.cityName} ${city.locations.size}")
            }
            places.postValue(spotList)
            model.loading(false) // spot list downloaded
        }
    }

    private fun showSpotList(bloodCenter: BloodCenter.Center) {
        querySpotList(bloodCenter.id)
        model.changeUiState(UiState.Spots)
    }

    private fun showSpotInfo(info: SpotInfo) {
        IntentHelper.showSpotInfo(this, info)
    }

    private fun showDonorInfo() {
        PrefsUtil.startBrowserActivity(
            this,
            Firebase.remoteConfig.getString("url_blood_donor_info")
        )
    }

    private fun versionInfo(): String {
        return PackageUtils.getPackageInfo(this, this::class.java)?.let { info ->
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode
            }
            "v${info.versionName} (r$code)"
        } ?: kotlin.run { "" }
    }
}