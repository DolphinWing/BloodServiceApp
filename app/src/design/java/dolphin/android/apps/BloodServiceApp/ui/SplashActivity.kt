package dolphin.android.apps.BloodServiceApp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dolphin.android.apps.BloodServiceApp.MyApplication
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil


class SplashActivity : Activity() {
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

        //FirebaseAnalytics.getInstance(this);//initialize this
        FirebaseRemoteConfig.getInstance()

        startMainActivity()
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
            dialog.setOnDismissListener { startMainActivity() }
            dialog.show()
            return false//don't show progress bar
        }

        return true
    }

    private fun startMainActivity() {
        var intent = Intent(this, MainActivity2::class.java)
        if (!PrefsUtil.isUseActivity2(baseContext)) {//just a fallback, we usually do use this
            intent = Intent(this, MainActivity::class.java)
        }
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
