package dolphin.android.apps.BloodServiceApp.pref;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;

import dolphin.android.apps.BloodServiceApp.R;
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper;

/**
 * Created by dolphin on 2014/10/26.
 * <p/>
 * Some utilities methods
 */
public class PrefsUtil {
    private final Context mContext;

    public PrefsUtil(Context context) {
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public boolean isEnableAdView() {
        return isEnableAdView(getContext());
    }

    public static boolean isEnableAdView(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(GeneralPreferenceFragment.KEY_ENABLE_ADVIEW, true);
    }

    public boolean isHeaderSticky() {
        return isHeaderSticky(getContext());
    }

    public static boolean isHeaderSticky(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(GeneralPreferenceFragment.KEY_HEADER_STICKY,
                context.getResources().getBoolean(R.bool.fragment_donation_sticky_grid_header_sticky));
    }

    //https://developer.chrome.com/multidevice/android/customtabs
    //https://github.com/GoogleChrome/custom-tabs-client
    public static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
    public static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";

    /**
     * start a browser activity
     *
     * @param context Context
     * @param url     url
     */
    public static void startBrowserActivity(Context context, String url) {
        if (context == null) {
            Log.e("BloodDataHelper", "no Context, no Activity");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (context.getResources().getBoolean(R.bool.feature_enable_chrome_custom_tabs)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //[169]dolphin++ add Chrome Custom Tabs
                Bundle extras = new Bundle();
                extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null);
                extras.putInt(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR,
                        ContextCompat.getColor(context, R.color.bloody_color));
                intent.putExtras(extras);
//            if (!isGoogleChromeInstalled(context)) {//for non-chrome app
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            }
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {//[97]dolphin++
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(context, R.string.query_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Check if Google Chrome is installed.
     *
     * @param context Context
     * @return true if installed
     */
    public static boolean isGoogleChromeInstalled(Context context) {
        if (context == null) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BloodDataHelper.URL_BASE_BLOOD_ORG));
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list != null && list.size() > 0) {
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
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo("com.google.android.apps.maps", 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isEnableSearchOnMap(Context context) {
        return PrefsUtil.isGoogleMapsInstalled(context)
                //| context.getResources().getBoolean(R.bool.feature_enable_search_on_map)
                && FirebaseRemoteConfig.getInstance().getBoolean("enable_search_on_map");
    }
}
