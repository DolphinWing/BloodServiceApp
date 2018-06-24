package dolphin.android.apps.BloodServiceApp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dolphin.android.apps.BloodServiceApp.BuildConfig
import dolphin.android.apps.BloodServiceApp.MyApplication
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil

class SplashActivity : Activity() {

    private lateinit var config: FirebaseRemoteConfig

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleUtil.onAttach(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        if (!checkGoogleApiAvailability()) {
            val myApp: MyApplication = application as MyApplication
            myApp.setGooglePlayServiceNotSupported()
            return
        }

        //startMainActivity()
        prepareRemoteConfig()
    }

    private fun checkGoogleApiAvailability(): Boolean {
        //http://stackoverflow.com/a/31016761/2673859
        //check google play service and authentication
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            //Log.e(TAG, googleAPI.getErrorString(result));
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
                        startMainActivity()
                    }
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (checkGoogleApiAvailability()) {
            startMainActivity()
        } else {
            finish()
        }
    }
}
