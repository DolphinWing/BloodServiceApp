package dolphin.android.apps.BloodServiceApp.pref;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStreamReader;

import dolphin.android.util.NoCoverageGenerated;

/**
 * Some utilities methods
 *
 * @hide
 * @deprecated replaced by data store
 */
@NoCoverageGenerated
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
        return pref.getInt("near_by_center", 0);
    }

    public void setCenterId(int id) {
        SharedPreferences pref = getDefaultPreference(mContext);
        pref.edit().putInt("near_by_center", id).apply();
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
