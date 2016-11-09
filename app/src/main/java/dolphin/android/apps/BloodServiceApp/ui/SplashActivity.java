package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
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

        Configuration config = getBaseContext().getResources().getConfiguration();
        //set default locale
        //http://stackoverflow.com/a/4239680
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList list = new LocaleList(Locale.TAIWAN);
            LocaleList.setDefault(list);
            config.setLocales(list);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            Configuration configuration = new Configuration();
//            configuration.setLocale(Locale.TAIWAN);
//            applyOverrideConfiguration(configuration);
        } else {
            config.locale = Locale.TAIWAN;
            Locale.setDefault(config.locale);
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        setContentView(R.layout.activity_splash);

        //FirebaseAnalytics.getInstance(this);//initialize this
        FirebaseRemoteConfig.getInstance();

        checkGoogleApiAvailability();
    }

    private void checkGoogleApiAvailability() {
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
            googleAPI.getErrorDialog(this, result, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            }).show();
            return;//don't show progress bar
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        checkGoogleApiAvailability();
    }
}
