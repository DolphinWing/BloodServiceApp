package dolphin.android.apps.BloodServiceApp;


import android.app.Application;
import android.content.Context;
import android.util.SparseArray;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;

import dolphin.android.apps.BloodServiceApp.provider.DonateDay;
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil;
import dolphin.android.apps.BloodServiceApp.provider.SpotList;

/**
 * Created by dolphin on 2014/10/21.
 * http://wangshifuola.blogspot.tw/2011/12/androidapplicationglobal-variable.html
 */
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.Helper.onAttach(base));
    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     * <p>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public MyApplication() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if (analytics == null) {
                return null;
            }

            // When dry run is set, hits will not be dispatched, but will still be logged as
            // though they were dispatched.
            analytics.setDryRun(getResources().getBoolean(R.bool.eng_mode));

            Tracker t = (trackerId == TrackerName.APP_TRACKER)
                    ? analytics.newTracker(getString(R.string.ga_trackingId))
                    : analytics.newTracker(R.xml.tracker_global);
            if (trackerId == TrackerName.APP_TRACKER) {
                t.enableAdvertisingIdCollection(true);
                t.enableAutoActivityTracking(true);
                t.enableExceptionReporting(true);
            }
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    //make a application level cache for quick switch, no need to pull from server in a short time
    private static class MyDonationList {
        ArrayList<DonateDay> days;
        long timestamp;
    }

    private static class MySpotList {
        SparseArray<SpotList> spots;
        SparseArray<String> cities;
        long timestamp;
    }

    private SparseArray<MyDonationList> mDonationList = new SparseArray<>();
    private SparseArray<HashMap<String, Integer>> mBloodStorage = null;
    private long mBloodStorageTimestamp = 0;
    private SparseArray<MySpotList> mSpotList = new SparseArray<>();

    public void setCacheDonationList(int siteId, ArrayList<DonateDay> days) {
        MyDonationList list = mDonationList.get(siteId);
        if (list == null) {
            list = new MyDonationList();
        }
        list.days = days;
        list.timestamp = System.currentTimeMillis();
        mDonationList.put(siteId, list);
    }

    public ArrayList<DonateDay> getCacheDonationList(int siteId) {
        MyDonationList list = mDonationList.get(siteId);
        if (list != null) {
            if ((System.currentTimeMillis() - list.timestamp) < 3600000) {
                return list.days;
            }
        }
        return null;
    }

    public void setCacheBloodStorage(SparseArray<HashMap<String, Integer>> array) {
        mBloodStorage = array;
        mBloodStorageTimestamp = System.currentTimeMillis();
    }

    public SparseArray<HashMap<String, Integer>> getCacheBloodStorage() {
        if ((System.currentTimeMillis() - mBloodStorageTimestamp) < 3600000) {
            return mBloodStorage;
        }
        return null;
    }

    public void setCacheSpotList(int siteId, SparseArray<SpotList> spots, SparseArray<String> cities) {
        MySpotList list = mSpotList.get(siteId);
        if (list == null) {
            list = new MySpotList();
        }
        list.spots = spots;
        list.timestamp = System.currentTimeMillis();
        list.cities = cities;
        mSpotList.put(siteId, list);
    }

    public SparseArray<SpotList> getCacheSpotList(int siteId) {
        MySpotList list = mSpotList.get(siteId);
        if (list != null) {
            if ((System.currentTimeMillis() - list.timestamp) < 3600000) {
                return list.spots;
            }
        }
        return null;
    }

    public SparseArray<String> getCacheCityList(int siteId) {
        MySpotList list = mSpotList.get(siteId);
        if (list != null) {
            if ((System.currentTimeMillis() - list.timestamp) < 3600000) {
                return list.cities;
            }
        }
        return null;
    }
}
