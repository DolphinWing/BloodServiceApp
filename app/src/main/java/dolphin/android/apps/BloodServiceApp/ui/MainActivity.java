package dolphin.android.apps.BloodServiceApp.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.DrawerAction;
import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;

public class MainActivity extends ActionBarActivity
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
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ActionView mActionView;
    private Toolbar mToolbar;
    private View mCustomView;
    private View mProgress;

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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionbar = getSupportActionBar();
        mCustomView = LayoutInflater.from(this).inflate(R.layout.fragment_toolbar, null);
        actionbar.setCustomView(mCustomView);
        actionbar.setDisplayShowCustomEnabled(true);

        mActionView = (ActionView) mCustomView.findViewById(R.id.nav_icon);
        mActionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mSectionsPagerAdapter.isAnySectionBusy()) {
                    return;//don't show drawer
                }
                //mActionView.setAction(new BackAction(), ActionView.ROTATE_COUNTER_CLOCKWISE);
                mNavigationDrawerFragment.openDrawer();
            }
        });
        mActionView.setVisibility(View.INVISIBLE);//set invisible at start

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mActionView);

        mProgress = findViewById(android.R.id.progress);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {//use ViewPager
            mViewPager.setAdapter(mSectionsPagerAdapter);

            LinePageIndicator indicator = (LinePageIndicator) findViewById(R.id.page_indicator);
            indicator.setViewPager(mViewPager);

            View text1 = findViewById(android.R.id.text1);
            text1.setTag(0);
            text1.setOnClickListener(onTabClickListener);
            View text2 = findViewById(android.R.id.text2);
            text2.setTag(1);
            text2.setOnClickListener(onTabClickListener);
        } else {//use panes
            View header = findViewById(R.id.page_header);
            header.setVisibility(View.GONE);
            View indicator = findViewById(R.id.page_indicator);
            indicator.setVisibility(View.GONE);
        }

        BloodDataHelper helper = new BloodDataHelper(this);
        TextView title = (TextView) findViewById(R.id.blood_center);
        if (title != null) {
            title.setText(helper.getBloodCenterName(mSiteId));
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sendGAOpenActivity();
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
            case R.id.action_settings:
                return true;
            case R.id.action_facebook:
				{//statics of click on Facebook button
					String siteName = null;
					TextView title = (TextView) findViewById(R.id.blood_center);
					if (title != null) {
						siteName = title.getText().toString();
					}
					sendGANavigationChanged("Facebook", siteName);
				}
                startActivity(BloodDataHelper.getOpenFacebookIntent(this, mSiteId));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        item = menu.findItem(R.id.action_go_next_week);
        item.setVisible(false);
        item = menu.findItem(R.id.action_go_last_week);
        item.setVisible(false);
        item = menu.findItem(R.id.action_go_back_today);
        item.setVisible(false);
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
        TextView title = (TextView) findViewById(R.id.blood_center);
        String siteName = helper.getBloodCenterName(mSiteId);
        if (title != null) {
            title.setText(siteName);
        }
        for (OnBloodCenterChanged listener : mListener) {
            listener.notifyChanged(mSiteId, 0);
        }
        sendGANavigationChanged(getString(R.string.title_section3), siteName);
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
            mActionView.setVisibility(View.INVISIBLE);
            mActionView.setEnabled(false);
        }
    }

    @Override
    public void onUpdateComplete(String id) {
        if (mSectionsPagerAdapter == null)
            return;
        mSectionsPagerAdapter.setSectionBusy(id, false);
        if (mProgress != null && !mSectionsPagerAdapter.isAnySectionBusy()) {
            mProgress.setVisibility(View.GONE);
            mActionView.setEnabled(true);
            mActionView.setVisibility(View.VISIBLE);
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
            mSectionMap = new HashMap<String, Boolean>();
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
        public void notifyChanged(int siteId, long timeInMillis);
    }

    private View.OnClickListener onTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = Integer.parseInt(view.getTag().toString());
            if (pos != mViewPager.getCurrentItem()) {
                mViewPager.setCurrentItem(pos);
            }
        }
    };

    //http://stackoverflow.com/a/12967721/2673859
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

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
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
        super.onBackPressed();
    }
}
