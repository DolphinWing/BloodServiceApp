package dolphin.android.apps.BloodServiceApp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Arrays;

import dolphin.android.apps.BloodServiceApp.R;

/**
 * Created by jimmyhu on 2017/2/22.
 * <p>
 * New Activity with new design layout for 3 Fragments.
 * Use BottomNavigation with NavigationDrawer to operate.
 */

public class MainActivity2 extends AppCompatActivity implements OnFragmentInteractionListener/*,
        NavigationDrawerFragment.NavigationDrawerCallbacks*/ {
    private final static String TAG = "MainActivity";

    private BaseListFragment mFragment;

    private FirebaseRemoteConfig mRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;

    private int[] mBloodCenterId;
    private int mSiteId = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBloodCenterId = getResources().getIntArray(R.array.blood_center_id);

        setContentView(R.layout.activity_navigation_spinner);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork()
                .build());

        //http://stackoverflow.com/documentation/android/7565/bottomnavigationview#t=201702220536238479271
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {//Attach the listener
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switchToSection(item.getItemId());
                            return true;
                        }
                    });
            //bottomNavigationView
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
            if (spinner != null) {
                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String[] centre = getResources().getStringArray(R.array.blood_center);
                ArrayList<String> list = new ArrayList<>(Arrays.asList(centre));
                list.remove(0);

                //http://stackoverflow.com/a/28348732
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        //android.R.layout.simple_list_item_1, list));
                        R.layout.spinner_textview, list);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_textview);
                spinner.setAdapter(adapter);

                spinner.setSelection(sp.getInt(NavigationDrawerFragment.PREF_USER_NEAR_BY_CENTER, 3));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        mSiteId = mBloodCenterId[position + 1];
                        sp.edit().putInt(NavigationDrawerFragment.PREF_USER_NEAR_BY_CENTER, position).apply();
                        //switchToSection(R.id.action_section2);
                        if (mFragment != null) {
                            mFragment.updateFragment(mSiteId, System.currentTimeMillis());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayUseLogoEnabled(true);
            actionbar.setIcon(R.drawable.ic_launcher);
//            actionbar.setDisplayHomeAsUpEnabled(true);
//            actionbar.setHomeButtonEnabled(true);
//            actionbar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
            actionbar.setDisplayShowTitleEnabled(false);
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                switchToSection(R.id.action_section2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //do something
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchToSection(int id) {
        long now = System.currentTimeMillis();
        switch (id) {
            case R.id.action_section1:
                mFragment = StorageFragment.newInstance(mSiteId, now);
                break;

            case R.id.action_section2:
                mFragment = DonationFragment.newInstance(mSiteId, now);
                break;

            case R.id.action_section3:
                mFragment = SpotFragment.newInstance(mSiteId, now);
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, mFragment).commit();
    }

    @Override
    public void onFragmentInteraction(String id) {
        //TODO: auto generated codes
    }

    @Override
    public void onUpdateStart(String id) {
        //TODO: auto generated codes
    }

    @Override
    public void onUpdateComplete(String id) {
        //TODO: auto generated codes
    }
}
