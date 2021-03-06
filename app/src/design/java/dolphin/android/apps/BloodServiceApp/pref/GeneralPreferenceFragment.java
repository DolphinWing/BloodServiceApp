package dolphin.android.apps.BloodServiceApp.pref;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.ui.SplashActivity;
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

    private boolean mEngMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        final Context context = getActivity().getBaseContext();
        mEngMode = getResources().getBoolean(R.bool.eng_mode);
        //dolphin++@2017.03.02, add Firebase support
        mEngMode |= FirebaseRemoteConfig.getInstance().getBoolean("enable_change_log_summary");
        PackageInfo pInfo = getPackageInfo(context);
        findPreference(KEY_APP_VERSION).setSummary(String.format(Locale.US,
                mEngMode ? "%s  r%d (eng)" : "%s (r%d)", pInfo.versionName, pInfo.versionCode));
    }

    public static PackageInfo getPackageInfo(Context context) {
        return PackageUtils.getPackageInfo(context, SplashActivity.class);
    }

    public static void showVersionSummary(Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(R.string.app_change_log);
        // windows Unicode file http://goo.gl/gRyTU
        dialog.setMessage(PrefsUtil.read_asset_text(activity, VERSION_FILE, VERSION_FILE_ENCODE));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
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
        } else if (key.equals(KEY_ENABLE_ACTIVITY2)) {
            showRestartAppDialog(getActivity());
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public static void showRestartAppDialog(final Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.title_restart_app_warning)
                .setMessage(R.string.message_restart_app)
                .setPositiveButton(R.string.action_restart_app_now,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(activity, SplashActivity.class);
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.action_restart_app_later, null)
                .create();
        dialog.show();
    }
}
