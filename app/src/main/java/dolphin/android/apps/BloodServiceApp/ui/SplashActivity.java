package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        if (getResources().getBoolean(R.bool.eng_mode)) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return;
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
