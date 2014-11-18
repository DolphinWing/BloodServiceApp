package dolphin.android.apps.BloodServiceApp.pref;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.util.AssetUtils;
import dolphin.android.util.PackageUtils;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends PreferenceFragment {
    private final static String TAG = "GeneralPreference";
    public static final String KEY_APP_VERSION = "app_version";
    private static final String VERSION_FILE = "version.txt";
    private static final String VERSION_FILE_ENCODE = "UTF-8";
    public final static String KEY_ENABLE_ADVIEW = "enable_adview";
    public final static String KEY_HEADER_STICKY = "enable_sticky_header";

    private boolean mEngMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

//        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//        // to their values. When their values change, their summaries are
//        // updated to reflect the new value, per the Android Design
//        // guidelines.
//        SettingsActivity.bindPreferenceSummaryToValue(findPreference("example_text"));
//        SettingsActivity.bindPreferenceSummaryToValue(findPreference("example_list"));

        final Context context = getActivity().getBaseContext();
        mEngMode = getResources().getBoolean(R.bool.eng_mode);
        PackageInfo pInfo = getPackageInfo(context);
        findPreference(KEY_APP_VERSION).setSummary(String.format(Locale.US,
                mEngMode ? "%s  r%d (eng)" : "%s (r%d)",
                pInfo.versionName, pInfo.versionCode));
    }

    public static PackageInfo getPackageInfo(Context context) {
        return PackageUtils.getPackageInfo(context, SettingsActivity.class);
    }

    public static void showVersionSummary(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(R.string.app_change_log);
        // windows Unicode file http://goo.gl/gRyTU
        dialog.setMessage(AssetUtils.read_asset_text(context,
                VERSION_FILE, VERSION_FILE_ENCODE));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // do nothing, just dismiss
                    }
                });
        dialog.show();

        // change AlertDialog message font size
        // http://stackoverflow.com/a/6563075
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(12);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            Log.wtf(TAG, "onPreferenceTreeClick key == null");//should not happen
        } else if (key.equals(KEY_APP_VERSION) && mEngMode) {
            showVersionSummary(getActivity());
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
