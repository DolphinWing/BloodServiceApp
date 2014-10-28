package dolphin.android.apps.BloodServiceApp.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        //context.getResources().getBoolean(R.bool.def_upcoming_game));
    }
}
