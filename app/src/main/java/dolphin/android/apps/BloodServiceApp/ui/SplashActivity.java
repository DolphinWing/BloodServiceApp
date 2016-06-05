package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

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

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        this.finish();
        overridePendingTransition(0, 0);
    }

}
