package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Locale;

import dolphin.android.apps.BloodServiceApp.R;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //set default locale
        //http://stackoverflow.com/a/4239680
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = Locale.TAIWAN;
        Locale.setDefault(config.locale);

        //FirebaseAnalytics.getInstance(this);//initialize this
        FirebaseRemoteConfig.getInstance();

        //http://stackoverflow.com/a/31016761/2673859
        //check google play service and authentication
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            //Log.e(TAG, googleAPI.getErrorString(result));
            TextView textView = (TextView) findViewById(android.R.id.message);
            if (textView != null) {
                textView.setText(googleAPI.getErrorString(result));
            }
            return;//don't show progress bar
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        this.finish();
        overridePendingTransition(0, 0);
    }

}
