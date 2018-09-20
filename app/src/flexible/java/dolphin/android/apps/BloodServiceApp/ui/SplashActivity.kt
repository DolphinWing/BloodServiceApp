package dolphin.android.apps.BloodServiceApp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dolphin.android.apps.BloodServiceApp.BuildConfig
import dolphin.android.apps.BloodServiceApp.MyApplication
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil
import java.lang.ref.WeakReference

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
    }

    private lateinit var config: FirebaseRemoteConfig
    private lateinit var handler: MyHandler

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleUtil.onAttach(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        handler = MyHandler(this)

        if (!checkGoogleApiAvailability()) {
            val myApp: MyApplication = application as MyApplication
            myApp.setGooglePlayServiceNotSupported()
            return
        }

        //startMainActivity()
        prepareRemoteConfig()
        handler.sendEmptyMessageDelayed(1, 1000) //show loading
    }

    private fun checkGoogleApiAvailability(): Boolean {
        //http://stackoverflow.com/a/31016761/2673859
        //check google play service and authentication
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            Log.e(TAG, googleAPI.getErrorString(result))
            val textView = findViewById<TextView>(android.R.id.message)
            textView.text = googleAPI.getErrorString(result)
            val dialog = googleAPI.getErrorDialog(this, result, 0)
            dialog.setOnDismissListener { prepareRemoteConfig() }
            dialog.show()
            return false//don't show progress bar
        }

        return true
    }

    private fun prepareRemoteConfig() {
        config = FirebaseRemoteConfig.getInstance()
        config.apply {
            setConfigSettings(FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build())
            setDefaults(R.xml.remote_config_defaults)
        }
        fetchFirebaseRemoteConfig()
    }

    private fun fetchFirebaseRemoteConfig() {
        config.fetch(if (BuildConfig.DEBUG) 60 else 43200)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        config.activateFetched()
                    }
                    checkPrivacyPolicyReview()
                }
    }

    private fun checkPrivacyPolicyReview() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val updateCode = FirebaseRemoteConfig.getInstance().getLong("privacy_policy_update_code")
        //if private policy has been updated
        if (!prefs.getBoolean(NavigationDrawerFragment.PREF_USER_LEARNED_DRAWER, false) &&
                prefs.getLong(MainActivity.PREF_PRIVATE_POLICY, 0) < updateCode) {
            findViewById<ViewStub>(android.R.id.list)?.inflate()
            findViewById<View>(android.R.id.text2)?.setOnClickListener {
                prefs.edit().putLong(MainActivity.PREF_PRIVATE_POLICY, updateCode).apply()
                startMainActivity()
            }
            //https://stackoverflow.com/q/5183645/2673859
            val text = getString(R.string.splash_privacy_policy_review)
            val target = getString(R.string.app_privacy_policy)
            val startIndex = text.indexOf(target)
            val span = Spannable.Factory.getInstance().newSpannable(text)
            span.setSpan(object : ClickableSpan() {
                override fun onClick(view: View) {
                    //Toast.makeText(this@SplashActivity, "!", Toast.LENGTH_SHORT).show()
                    SettingsFragment.showPrivacyPolicyReview(this@SplashActivity)
                }
            }, startIndex, startIndex + target.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            findViewById<TextView>(android.R.id.text1)?.apply {
                setText(span)
                movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            startMainActivity() //everything is ready
        }
        handler.removeMessages(1) //cancel delay message to show loading
        handler.sendEmptyMessage(0) //hide loading
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (checkGoogleApiAvailability()) {
            prepareRemoteConfig()
            handler.sendEmptyMessage(1) //show loading
        } else {
            Log.e(TAG, "onActivityResult still no google api")
            finish()
        }
    }

    private class MyHandler(a: SplashActivity) : Handler() {
        private val activity = WeakReference(a)
        override fun handleMessage(msg: Message?) {
            activity.get()?.handleMessage(msg)
        }
    }

    internal fun handleMessage(msg: Message?) {
        findViewById<View>(android.R.id.progress)?.visibility = if (msg?.what == 1) View.VISIBLE else View.INVISIBLE
    }
}
