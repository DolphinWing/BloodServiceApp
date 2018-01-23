package dolphin.android.apps.BloodServiceApp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;

import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil;
import dolphin.android.apps.BloodServiceApp.pref.SettingsActivity;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil;

/**
 * Created by jimmyhu on 2017/2/22.
 * <p>
 * New Activity with new design layout for 3 Fragments.
 * Use BottomNavigation with NavigationDrawer to operate.
 */

public class MainActivity2 extends AppCompatActivity implements OnFragmentInteractionListener {
    private final static String TAG = "MainActivity";

    private BaseListFragment mFragment;

    private FirebaseRemoteConfig mRemoteConfig;
    private FirebaseAnalytics mAnalytics;

    private int[] mBloodCenterId;
    private int mSiteId = 5;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtil.Helper.onAttach(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBloodCenterId = getResources().getIntArray(R.array.blood_center_id);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int position = sp.getInt(NavigationDrawerFragment.PREF_USER_NEAR_BY_CENTER, 3);
        mSiteId = mBloodCenterId[position + 1];

        setContentView(R.layout.activity_navigation_spinner);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork()
                .build());

        //http://stackoverflow.com/documentation/android/7565/bottomnavigationview#t=201702220536238479271
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Spinner spinner = toolbar.findViewById(R.id.spinner_nav);
            if (spinner != null) {
                //final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String[] centre = getResources().getStringArray(R.array.blood_center);
                ArrayList<String> list = new ArrayList<>(Arrays.asList(centre));
                list.remove(0);

                //http://stackoverflow.com/a/28348732
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        //android.R.layout.simple_list_item_1, list));
                        R.layout.spinner_textview, list);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_textview);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        mSiteId = mBloodCenterId[position + 1];
                        sp.edit().putInt(NavigationDrawerFragment.PREF_USER_NEAR_BY_CENTER, position).apply();
                        //switchToSection(R.id.action_section2);
                        if (mFragment != null) {
                            mFragment.updateFragment(mSiteId, System.currentTimeMillis());
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM,
                                BloodDataHelper.getBloodCenterName(getBaseContext(), mSiteId));
                        logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                spinner.setSelection(position);
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

        final MyApplication application = (MyApplication) getApplication();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                switchToSection(R.id.action_section2);
                if (application.isGooglePlayServiceSupported()) {//only fetch it when it is installed
                    prepareRemoteConfig();
                } else {//only use default values
                    mRemoteConfig = FirebaseRemoteConfig.getInstance();
                    mRemoteConfig.setDefaults(R.xml.remote_config_defaults);
                }
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
        Bundle bundle = new Bundle();
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //do something
                return true;
            case R.id.action_facebook:
                startActivity(BloodDataHelper.getOpenFacebookIntent(this, mSiteId));

                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
                        BloodDataHelper.getBloodCenterName(this, mSiteId));
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Facebook");
                logEvent(FirebaseAnalytics.Event.SHARE, bundle);

                return true;
            case R.id.action_go_to_website: {
                Intent intent = BloodDataHelper.getOpenBloodCalendarSourceIntent(this, mSiteId);
                if (intent != null) {
                    startActivity(intent);
                }

                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
                        BloodDataHelper.getBloodCenterName(this, mSiteId));
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,
                        getString(R.string.action_go_to_website));
                logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
            return true;//break;
            case R.id.action_personal:
                PrefsUtil.startBrowserActivity(this,
                        FirebaseRemoteConfig.getInstance().getString("url_blood_donor_info"));

                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
                        BloodDataHelper.getBloodCenterName(this, mSiteId));
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,
                        getString(R.string.action_go_to_personal));
                logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;//break;
        }
        return super.onOptionsItemSelected(item);
    }

    //private SparseArray<BaseListFragment> mFragmentCache = new SparseArray<>();

    private void switchToSection(int id) {
        //mFragment = mFragmentCache.get(id);
        long now = System.currentTimeMillis();
        //if (mFragment == null) {
        switch (id) {
            case R.id.action_section1:
                mFragment = StorageFragment.newInstance(mSiteId, now);
                break;

            case R.id.action_section2:
                mFragment = DonationFragment.newInstance(mSiteId, now);
                break;

            case R.id.action_section3:
                //mFragment = SpotFragment.newInstance(mSiteId, now);
                mFragment = SpotListFragment.Factory.create(mSiteId, now);
                break;
            default:
                return;
        }
        //    mFragmentCache.put(id, mFragment);
        //}
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, mFragment).commitAllowingStateLoss();
        //dolphin++ I can allow state loss

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,
                BloodDataHelper.getBloodCenterName(this, mSiteId));
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(id));
        logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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

//    //http://stackoverflow.com/a/12967721/2673859
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        int orientation = this.getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Log.v(TAG, "landscape");//FIXME: do something?
//        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Log.v(TAG, "portrait");//FIXME: do something?
//        }
//    }

    @SuppressLint("MissingPermission")
    private void prepareRemoteConfig() {
        mAnalytics = FirebaseAnalytics.getInstance(this);

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(getResources().getBoolean(R.bool.eng_mode))
                .build();
        mRemoteConfig.setConfigSettings(configSettings);
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchRemoteConfig();
    }

    /**
     * Fetch RemoteConfig from server.
     */
    private void fetchRemoteConfig() {
        long cacheExpiration = 43200; // 12 hours in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 60;
        }

        // [START fetch_config_with_callback]
        final long start = System.currentTimeMillis();
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        long cost = System.currentTimeMillis() - start;
                        if (task.isSuccessful()) {
                            Log.v(TAG, String.format("Fetch Succeeded: %s ms", cost));
                            // Once the config is successfully fetched it must be activated before
                            // newly fetched values are returned.
                            mRemoteConfig.activateFetched();
                        } else {
                            Log.e(TAG, "Fetch failed");
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }

    private void logEvent(String event, Bundle data) {
        if (mAnalytics == null) {
            return;//don't log any event
        }

        mAnalytics.logEvent(event, data);
    }
}
