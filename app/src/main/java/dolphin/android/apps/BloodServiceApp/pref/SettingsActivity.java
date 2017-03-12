package dolphin.android.apps.BloodServiceApp.pref;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;
import java.util.Locale;

import dolphin.android.apps.BloodServiceApp.MyApplication;
import dolphin.android.apps.BloodServiceApp.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private boolean enableNotification = false;

    //This API was added due to a newly discovered vulnerability.
    // Please see http://ibm.co/1bAA8kF or http://ibm.co/IDm2Es
    @TargetApi(Build.VERSION_CODES.KITKAT)
    //http://stackoverflow.com/a/20494759/2673859
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return GeneralPreferenceFragment.class.getName().equals(fragmentName)
//                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || OpenSourceFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableNotification = getResources().getBoolean(R.bool.feature_notification);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //setupSimplePreferencesScreen();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setupSimplePreferencesScreen();
            }
        });
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);
        updateVersionSummary();

        if (enableNotification) {
            // Add 'notifications' preferences, and a corresponding header.
            PreferenceCategory fakeHeader = new PreferenceCategory(this);
            fakeHeader.setTitle(R.string.pref_header_notifications);
            getPreferenceScreen().addPreference(fakeHeader);
            addPreferencesFromResource(R.xml.pref_notification);
        }

        addPreferencesFromResource(R.xml.pref_open_source);

        if (PrefsUtil.isUseActivity2(this)) {//[61]dolphin++
            PreferenceGroup group = (PreferenceGroup) findPreference(OpenSourceFragment.KEY_OPEN_SOURCE_GROUP);
            if (group != null) {
                Preference p1 = findPreference(OpenSourceFragment.KEY_OPEN_SOURCE_CIRCLE_IMAGE);
                if (p1 != null) {
                    group.removePreference(p1);
                }
                Preference p2 = findPreference(OpenSourceFragment.KEY_CREDIT_BACKGROUND);
                if (p2 != null) {
                    group.removePreference(p2);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);

            if (!getResources().getBoolean(R.bool.feature_notification)) {
                target.remove(1);
            }
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
        }
    }

    private void updateVersionSummary() {
        PackageInfo pInfo = GeneralPreferenceFragment.getPackageInfo(getBaseContext());
        Preference pref = findPreference(GeneralPreferenceFragment.KEY_APP_VERSION);
        if (pref != null) {
            pref.setSummary(String.format(Locale.US, "%s (r%d)",
                    pInfo.versionName, pInfo.versionCode));
        }
    }
    
    private void sendGAOpenActivity() {
        // Get tracker.
        Tracker t = ((MyApplication) getApplication()).getTracker(
                MyApplication.TrackerName.APP_TRACKER);
        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName("BloodServiceApp.SettingsActivity");
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        // Clear the screen name field when we're done.
        t.setScreenName(null);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            Log.wtf("SettingsActivity", "onPreferenceTreeClick key == null");//should not happen
        } else if (key.equals(GeneralPreferenceFragment.KEY_APP_VERSION)
                && FirebaseRemoteConfig.getInstance().getBoolean("enable_change_log_summary")) {
            GeneralPreferenceFragment.showVersionSummary(this);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
