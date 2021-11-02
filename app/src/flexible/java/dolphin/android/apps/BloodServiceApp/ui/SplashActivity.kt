@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.view.ViewTreeObserver.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dolphin.android.apps.BloodServiceApp.BuildConfig
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil
import dolphin.android.util.PackageUtils
import java.lang.ref.WeakReference

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
    }

    private lateinit var config: FirebaseRemoteConfig
    private lateinit var handler: MyHandler
    private val ready = MutableLiveData(false)

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleUtil.onAttach(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        handler = MyHandler(this)

        findViewById<TextView>(android.R.id.title)?.apply {
            val packageInfo = PackageUtils.getPackageInfo(context, this::class.java)
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode?.toString()
            } else {
                packageInfo?.versionCode?.toString()
            } ?: "-"
        }

        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupPreDrawListener()
        }

        if (!checkGoogleApiAvailability()) {
//            val myApp: MyApplication = application as MyApplication
//            myApp.setGooglePlayServiceNotSupported()
            return
        }

        // startMainActivity()
        prepareRemoteConfig()
        handler.sendEmptyMessageDelayed(1, 1000) // show loading
    }

    private fun checkGoogleApiAvailability(): Boolean {
        // http://stackoverflow.com/a/31016761/2673859
        // check google play service and authentication
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            Log.e(TAG, googleAPI.getErrorString(result))
            val textView = findViewById<TextView>(android.R.id.message)
            textView.text = googleAPI.getErrorString(result)
            val dialog = googleAPI.getErrorDialog(this, result, 0)
            dialog?.setOnDismissListener { prepareRemoteConfig() }
            dialog?.show()
            return false // don't show progress bar
        }

        return true
    }

    private fun prepareRemoteConfig() {
        // Google Mobile Ads SDK version 17.0.0
        MobileAds.initialize(this) { status ->
            status.adapterStatusMap.values.forEach { s ->
                Log.v(TAG, "MobileAds: ${s.initializationState.name} ${s.description}")
            }
        }

        config = FirebaseRemoteConfig.getInstance()
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 60 else 43200)
            .build()
        config.setConfigSettingsAsync(settings)
        config.setDefaultsAsync(R.xml.remote_config_defaults)
        fetchFirebaseRemoteConfig()
    }

    private fun fetchFirebaseRemoteConfig() {
        config.fetchAndActivate().addOnCompleteListener {
            checkPrivacyPolicyReview()
        }
    }

    private fun checkPrivacyPolicyReview() {
        val prefs = PrefsUtil.getDefaultPreference(this)
        val updateCode = FirebaseRemoteConfig.getInstance().getLong("privacy_policy_update_code")
        // if private policy has been updated
        if (!prefs.getBoolean(NavigationDrawerFragment.PREF_USER_LEARNED_DRAWER, false) &&
            prefs.getLong(MainActivity.PREF_PRIVATE_POLICY, 0) < updateCode
        ) {
            findViewById<ViewStub>(android.R.id.list)?.inflate()
            findViewById<View>(android.R.id.text2)?.setOnClickListener {
                prefs.edit().putLong(MainActivity.PREF_PRIVATE_POLICY, updateCode).apply()
                startMainActivity() // confirm policy
            }
            // https://stackoverflow.com/q/5183645/2673859
            val text = getString(R.string.splash_privacy_policy_review)
            val target = getString(R.string.app_privacy_policy)
            val startIndex = text.indexOf(target)
            val span = Spannable.Factory.getInstance().newSpannable(text)
            span.setSpan(
                object : ClickableSpan() {
                    override fun onClick(view: View) {
                        // Toast.makeText(this@SplashActivity, "!", Toast.LENGTH_SHORT).show()
                        SettingsFragment.showPrivacyPolicyReview(this@SplashActivity)
                    }
                },
                startIndex, startIndex + target.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            findViewById<TextView>(android.R.id.text1)?.apply {
                setText(span)
                movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            startMainActivity() // everything is ready
        }
        handler.removeMessages(1) // cancel delay message to show loading
        handler.sendEmptyMessage(0) // hide loading
    }

    private fun startMainActivity() {
        ready.postValue(true)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (checkGoogleApiAvailability()) {
            prepareRemoteConfig()
            handler.sendEmptyMessage(1) // show loading
        } else {
            Log.e(TAG, "onActivityResult still no google api")
            finish()
        }
    }

    private class MyHandler(a: SplashActivity) : Handler(Looper.getMainLooper()) {
        private val activity = WeakReference(a)

        override fun handleMessage(msg: Message) {
            activity.get()?.handleMessage(msg)
        }
    }

    private fun handleMessage(msg: Message) {
        findViewById<View>(android.R.id.progress)?.visibility =
            if (msg.what == 1) View.VISIBLE else View.INVISIBLE
    }

    private fun setupPreDrawListener() {
        // https://developer.android.com/about/versions/12/features/splash-screen#implement
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Check if the initial data is ready.
                return if (ready.value == true) {
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
}
