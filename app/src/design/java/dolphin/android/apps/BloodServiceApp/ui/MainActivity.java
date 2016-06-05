package dolphin.android.apps.BloodServiceApp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.DrawerAction;
import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;

public class MainActivity extends AppCompatActivity//ActionBarActivity
        implements OnFragmentInteractionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {
    private final static String TAG = "MainActivity";
    private int[] mBloodCenterId;
    private int mSiteId = 5;
    private final List<OnBloodCenterChanged> mListener = new ArrayList<OnBloodCenterChanged>();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ActionView mActionView;
    private Toolbar mToolbar;
    private View mCustomView;
    private View mProgress;

    private FirebaseRemoteConfig mRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBloodCenterId = getResources().getIntArray(R.array.blood_center_id);
        //mListener = new ArrayList<OnBloodCenterChanged>();

        setContentView(R.layout.activity_navigation_drawer);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        ActionBar actionbar = getSupportActionBar();
//        mCustomView = LayoutInflater.from(this).inflate(R.layout.fragment_toolbar, null);
//        actionbar.setCustomView(mCustomView);
//        actionbar.setDisplayShowCustomEnabled(true);

//        mActionView = (ActionView) mCustomView.findViewById(R.id.nav_icon);
//        mActionView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if (mSectionsPagerAdapter.isAnySectionBusy()) {
//                    return;//don't show drawer
//                }
//                //mActionView.setAction(new BackAction(), ActionView.ROTATE_COUNTER_CLOCKWISE);
//                mNavigationDrawerFragment.openDrawer();
//            }
//        });
//        mActionView.setVisibility(View.INVISIBLE);//set invisible at start

        if (mNavigationDrawerFragment != null) {// Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout), mActionView);
        }
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        }

        mProgress = findViewById(android.R.id.progress);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {//use ViewPager
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            if (tabLayout != null) {
                tabLayout.setupWithViewPager(mViewPager);
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        } else {//use panes
            View header = findViewById(R.id.page_header);
            if (header != null) {
                header.setVisibility(View.GONE);
            }
            View indicator = findViewById(R.id.page_indicator);
            if (indicator != null) {
                indicator.setVisibility(View.GONE);
            }
        }

        BloodDataHelper helper = new BloodDataHelper(this);
        TextView title = (TextView) findViewById(R.id.blood_center);
        if (title != null) {
            title.setText(helper.getBloodCenterName(mSiteId));
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //sendGAOpenActivity();
                prepareRemoteConfig();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                if (!mSectionsPagerAdapter.isAnySectionBusy()) {
                    mNavigationDrawerFragment.openDrawer();
                }
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_facebook: {//statics of click on Facebook button
                String siteName = null;
                TextView title = (TextView) findViewById(R.id.blood_center);
                if (title != null) {
                    siteName = title.getText().toString();
                }
                sendGANavigationChanged("Facebook", siteName);

                if (mFirebaseAnalytics != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, siteName);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Facebook");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
                }
            }
            startActivity(BloodDataHelper.getOpenFacebookIntent(this, mSiteId));
            return true;
            case R.id.action_go_to_website:
                Intent intent = BloodDataHelper.getOpenBloodCalendarSourceUrl(this, mSiteId);
                if (intent != null) {
                    startActivity(intent);
                    return true;
                }
                break;
            case R.id.action_go_to_station: {
                //get blood center id's matching index
                int[] Ids = getResources().getIntArray(R.array.blood_center_id);
                int i;
                for (i = Ids.length - 1; i > 0; i--) {
                    if (Ids[i] == mSiteId) {
                        break;
                    }
                }
                //use the index to get real url
                String[] urls = getResources().getStringArray(R.array.blood_center_donate_station);
                if (urls.length > i) {
                    PrefsUtil.startBrowserActivity(this, urls[i]);
                    return true;
                }
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        if (item != null) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.action_go_next_week);
        if (item != null) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.action_go_last_week);
        if (item != null) {
            item.setVisible(false);
        }
        item = menu.findItem(R.id.action_go_back_today);
        if (item != null) {
            item.setVisible(false);
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mActionView != null) {
            mActionView.setAction(new DrawerAction(), ActionView.ROTATE_CLOCKWISE);
        }
        //Log.d(TAG, String.format("pos=%d, site id=%d", position,
        //        mBloodCenterId[position + 1]));
        mSiteId = mBloodCenterId[position + 1];
        BloodDataHelper helper = new BloodDataHelper(this);
//        TextView title = (TextView) findViewById(R.id.blood_center);
        String siteName = helper.getBloodCenterName(mSiteId);
//        if (title != null) {
//            title.setText(siteName);
//        }
        setTitle(siteName);

        for (OnBloodCenterChanged listener : mListener) {
            listener.notifyChanged(mSiteId, 0);
        }
        sendGANavigationChanged(getString(R.string.title_section3), siteName);

        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, siteName);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        }
    }

    @Override
    public void onFragmentInteraction(String id) {
        //TODO: auto generated codes
    }

    @Override
    public void onUpdateStart(String id) {
        if (mSectionsPagerAdapter != null) {
            mSectionsPagerAdapter.setSectionBusy(id, true);
        }
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
            if (mActionView != null) {
                mActionView.setVisibility(View.INVISIBLE);
                mActionView.setEnabled(false);
            }
        }
    }

    @Override
    public void onUpdateComplete(String id) {
        if (mSectionsPagerAdapter == null) {
            return;
        }
        mSectionsPagerAdapter.setSectionBusy(id, false);
        if (mProgress != null && !mSectionsPagerAdapter.isAnySectionBusy()) {
            mProgress.setVisibility(View.GONE);
            if (mActionView != null) {
                mActionView.setEnabled(true);
                mActionView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private HashMap<String, Boolean> mSectionMap;

//        public boolean isSectionBusy(String id) {
//            if (mSectionMap.containsKey(id))
//                return mSectionMap.get(id);
//            return false;
//        }

        public boolean isAnySectionBusy() {
            return mSectionMap.containsValue(true);
        }

        public void setSectionBusy(String id, boolean b) {
            mSectionMap.put(id, b);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mSectionMap = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment;
            long now = System.currentTimeMillis();
            switch (position) {
                case 0:
                    fragment = StorageFragment.newInstance(mSiteId, now);
                    break;
                case 1:
                    fragment = DonationFragment.newInstance(mSiteId, now);
                    break;
                default:
                    fragment = PlaceholderFragment.newInstance(position + 1);
                    break;
            }
            if (fragment instanceof BaseListFragment) {
                mSectionMap.put(((BaseListFragment) fragment).getFragmentId(), false);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                //case 2:
                //    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    public void registerOnBloodCenterChanged(OnBloodCenterChanged listener) {
        //if (mListener == null) {
        //    Log.w(TAG, "registerOnBloodCenterChanged null");
        //    mListener = new ArrayList<OnBloodCenterChanged>();
        //}
        if (listener != null) {
            mListener.add(listener);
        }
    }

    public void unregisterOnBloodCenterChanged(OnBloodCenterChanged listener) {
        if (listener != null) {
            mListener.remove(listener);
        }
    }

    public interface OnBloodCenterChanged {
        void notifyChanged(int siteId, long timeInMillis);
    }

    //http://stackoverflow.com/a/12967721/2673859
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(TAG, "landscape");//FIXME: do something?
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(TAG, "portrait");//FIXME: do something?
        }
    }

    private void sendGAOpenActivity() {
        // Get tracker.
        Tracker t = ((MyApplication) getApplication()).getTracker(
                MyApplication.TrackerName.GLOBAL_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("BloodServiceApp.MainActivity");
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        // Clear the screen name field when we're done.
        t.setScreenName(null);
    }

    private void sendGANavigationChanged(String action, String name) {
        // Get tracker.
        Tracker t = ((MyApplication) getApplication()).getTracker(
                MyApplication.TrackerName.GLOBAL_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("BloodServiceApp.MainActivity");
        // This event will also be sent with &cd=Home%20Screen.
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("UI")
                .setAction(action)
                .setLabel(name)
                .build());
        // Clear the screen name field when we're done.
        t.setScreenName(null);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    private void prepareRemoteConfig() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
}
