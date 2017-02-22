package dolphin.android.apps.BloodServiceApp;


import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by dolphin on 2014/10/21.
 * http://wangshifuola.blogspot.tw/2011/12/androidapplicationglobal-variable.html
 */
public class MyApplication extends Application {

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

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

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
}
