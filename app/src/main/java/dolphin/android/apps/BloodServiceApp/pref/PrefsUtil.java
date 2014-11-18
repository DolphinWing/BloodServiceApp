package dolphin.android.apps.BloodServiceApp.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dolphin.android.apps.BloodServiceApp.R;

/**
 * Created by dolphin on 2014/10/26.
 */
public class PrefsUtil {
    private Context mContext;

    public PrefsUtil(Context context) {
        mContext = context;
    }

    public boolean isEnableAdView() {
        return isEnableAdView(mContext);
    }

    public static boolean isEnableAdView(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(GeneralPreferenceFragment.KEY_ENABLE_ADVIEW, true);
    }

    public boolean isHeaderSticky() {
        return isHeaderSticky(mContext);
    }

    public static boolean isHeaderSticky(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(GeneralPreferenceFragment.KEY_HEADER_STICKY,
                context.getResources().getBoolean(R.bool.fragment_donation_sticky_grid_header_sticky));
    }
}
