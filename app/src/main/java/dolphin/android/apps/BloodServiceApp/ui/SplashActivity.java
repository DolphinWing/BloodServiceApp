package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

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

//        if (getResources().getBoolean(R.bool.eng_mode)) {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
//        return;
//        }

/*        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BloodDataHelper helper = new BloodDataHelper(getBaseContext());
                helper.getLatestWeekCalendar(5);
            }
        });*/
    }

}
