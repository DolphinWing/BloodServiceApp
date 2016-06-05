package dolphin.android.apps.BloodServiceApp.pref;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;

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
    public static final String KEY_OPEN_SOURCE_SUPER_SLIM = "open_super_slim";
    public static final String KEY_CREDIT_BACKGROUND = "credit_background2";

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
            //Intent intent = new Intent(Intent.ACTION_VIEW);
            if (key == null) {
                Log.wtf(TAG, "onPreferenceTreeClick key == null");//should not happen
            } else if (key.equals(KEY_OPEN_SOURCE_OKHTTP)) {
                //intent.setData(Uri.parse("http://square.github.io/okhttp/"));
                PrefsUtil.startBrowserActivity(getActivity(),
                        "http://square.github.io/okhttp/");
            } else if (key.equals(KEY_OPEN_SOURCE_STICKY_GRID)) {
                Log.d(TAG, "FIXME: sticky grid");
            } else if (key.equals(KEY_OPEN_SOURCE_CIRCLE_IMAGE)) {
                //intent.setData(Uri.parse("https://github.com/markushi/android-ui"));
                PrefsUtil.startBrowserActivity(getActivity(),
                        "https://github.com/markushi/android-ui");
            } else if (key.equals(KEY_OPEN_SOURCE_ACTION_VIEW)) {
                Log.d(TAG, "FIXME: action view");
            } else if (key.equals(KEY_OPEN_SOURCE_SUPER_SLIM)) {
                //intent.setData(Uri.parse("https://github.com/TonicArtos/SuperSLiM"));
                PrefsUtil.startBrowserActivity(getActivity(),
                        "https://github.com/TonicArtos/SuperSLiM");
            } else if (key.equals(KEY_CREDIT_BACKGROUND)) {
                //intent.setData(Uri.parse("https://www.flickr.com/photos/makelessnoise/2562431372/"));
                PrefsUtil.startBrowserActivity(getActivity(),
                        "https://www.flickr.com/photos/makelessnoise/2562431372/");
            }
//            //[46]dolphin++ add Chrome Custom Tabs
//            Bundle extras = new Bundle();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                extras.putBinder(BloodDataHelper.EXTRA_CUSTOM_TABS_SESSION, null);
//            }
//            extras.putInt(BloodDataHelper.EXTRA_CUSTOM_TABS_TOOLBAR_COLOR,
//                    getResources().getColor(R.color.bloody_color));
//            intent.putExtras(extras);
//            startActivity(intent);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
