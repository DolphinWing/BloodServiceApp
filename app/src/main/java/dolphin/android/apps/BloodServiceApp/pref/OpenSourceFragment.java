package dolphin.android.apps.BloodServiceApp.pref;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import dolphin.android.apps.BloodServiceApp.R;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OpenSourceFragment extends PreferenceFragment {
    private final static String TAG = "GeneralPreference";
    public static final String KEY_OPEN_SOURCE_OKHTTP = "open_okhttp";
    public static final String KEY_OPEN_SOURCE_STICKY_GRID = "open_stickygridheaders";
    public static final String KEY_OPEN_SOURCE_CIRCLE_IMAGE = "open_markushi_android_ui";
    public static final String KEY_OPEN_SOURCE_ACTION_VIEW = "open_circleimageview";

    private boolean mShowOpenSource = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_open_source);

        mShowOpenSource = getResources().getBoolean(R.bool.eng_mode);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (mShowOpenSource) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (key == null) {
                Log.wtf(TAG, "onPreferenceTreeClick key == null");//should not happen
            } else if (key.equals(KEY_OPEN_SOURCE_OKHTTP)) {
                //TODO show url
            } else if (key.equals(KEY_OPEN_SOURCE_STICKY_GRID)) {
                //TODO show url
            } else if (key.equals(KEY_OPEN_SOURCE_CIRCLE_IMAGE)) {
                //TODO show url
            } else if (key.equals(KEY_OPEN_SOURCE_ACTION_VIEW)) {
                //TODO show url
            }
            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
