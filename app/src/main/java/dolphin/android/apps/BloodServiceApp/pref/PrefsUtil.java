package dolphin.android.apps.BloodServiceApp.pref;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;


/**
 * Created by dolphin on 2014/10/26.
 * <p/>
 * Some utilities methods
 */
public class PrefsUtil {
    private final Context mContext;
    private final static String KEY_ENABLE_ADVIEW = "enable_adview";
    private final static String KEY_HEADER_STICKY = "enable_sticky_header";
    private final static String KEY_ENABLE_ACTIVITY2 = "enable_activity2";

    public PrefsUtil(Context context) {
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static SharedPreferences getDefaultPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Read preference value about AdView
     *
     * @return true if user enables AdView
     */
    @SuppressWarnings("unused")
    public boolean isEnableAdView() {
        return isEnableAdView(getContext());
    }

    /**
     * Read preference value about AdView
     *
     * @param context Context
     * @return true if user enables AdView
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isEnableAdView(Context context) {
        if (context == null) return true;
        SharedPreferences pref = getDefaultPreference(context);
        return pref.getBoolean(KEY_ENABLE_ADVIEW, true);
    }

    /**
     * Check if use sticky header. only useful in stickgrid build
     *
     * @return true if need stick header
     */
    @SuppressWarnings("unused")
    public boolean isHeaderSticky() {
        return isHeaderSticky(getContext());
    }

    /**
     * Check if use sticky header.
     *
     * @param context Context
     * @return true if need stick header
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isHeaderSticky(Context context) {
        if (context == null) return false;
        SharedPreferences pref = getDefaultPreference(context);
        return pref.getBoolean(KEY_HEADER_STICKY, true);
    }

    /**
     * Check if user want to use old ViewPager+Tab presentation.
     *
     * @param context Context
     * @return true if we use new BottomNavigation presentation.
     */
    @SuppressWarnings("unused")
    public static boolean isUseActivity2(Context context) {
        if (context == null) return true;
        SharedPreferences pref = getDefaultPreference(context);
        return pref.getBoolean(KEY_ENABLE_ACTIVITY2, true);
    }

    public long getPolicyCode() {
        SharedPreferences pref = getDefaultPreference(mContext);
        return pref.getLong("private_policy", 0);
    }

    public void setPolicyCode(long code) {
        SharedPreferences pref = getDefaultPreference(mContext);
        pref.edit().putLong("private_policy", code).apply();
    }

    public int getCenterId() {
        SharedPreferences pref = getDefaultPreference(mContext);
        return pref.getInt("near_by_center", 5);
    }

    public void setCenterId(int id) {
        SharedPreferences pref = getDefaultPreference(mContext);
        pref.edit().putInt("near_by_center", id).apply();
    }

    //https://developer.chrome.com/multidevice/android/customtabs
    //https://github.com/GoogleChrome/custom-tabs-client
    public static final String EXTRA_CUSTOM_TABS_SESSION =
            "android.support.customtabs.extra.SESSION";
    public static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR =
            "android.support.customtabs.extra.TOOLBAR_COLOR";

    /**
     * start a browser activity
     *
     * @param context Context
     * @param url     url
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void startBrowserActivity(Context context, String url) {
        if (context == null) {
            Log.e("BloodDataHelper", "no Context, no Activity");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if Google Chrome is installed.
     *
     * @param context Context
     * @return true if installed
     */
    @SuppressWarnings("unused")
    public static boolean isGoogleChromeInstalled(Context context) {
        if (context == null) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(BloodDataHelper.URL_BASE_BLOOD_ORG));
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            for (ResolveInfo resolveInfo : list) {
                //Log.d("CpblCalendarHelper", resolveInfo.activityInfo.packageName);
                if (resolveInfo.activityInfo.packageName.startsWith("com.android.chrome")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * http://wp.me/p2XxfD-1u
     *
     * @return true if installed
     */
    public static boolean isGoogleMapsInstalled(Context context) {
        if (context == null) {
            return false;
        }
        try {
            context.getPackageManager()
                    .getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static boolean isEnableSearchOnMap(Context context) {
        return PrefsUtil.isGoogleMapsInstalled(context)
                //| context.getResources().getBoolean(R.bool.feature_enable_search_on_map)
                && FirebaseRemoteConfig.getInstance().getBoolean("enable_search_on_map");
    }

    public static String read_asset_text(Context context, String asset_name, String encoding) {
        try {
            InputStreamReader sr =
                    new InputStreamReader(context.getAssets().open(asset_name),
                            (encoding != null) ? encoding : "UTF8");
            //Log.i(TAG, asset_name + " " + sr.getEncoding());

            int len;
            StringBuilder sb = new StringBuilder();

            while (true) {//read from buffer
                char[] buffer = new char[1024];
                len = sr.read(buffer);//, size, 512);
                //Log.d(TAG, String.format("%d", len));
                if (len > 0) {
                    sb.append(buffer);
                } else {
                    break;
                }
            }
            //Log.i(TAG, String.format("  length = %d", sb.length()));

            sr.close();
            return sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
