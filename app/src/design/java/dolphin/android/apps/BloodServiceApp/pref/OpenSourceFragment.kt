package dolphin.android.apps.BloodServiceApp.pref

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceGroup
import android.preference.PreferenceScreen
import android.util.Log

import dolphin.android.apps.BloodServiceApp.R

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class OpenSourceFragment : PreferenceFragment() {

    private var mShowOpenSource = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_open_source)

        mShowOpenSource = resources.getBoolean(R.bool.eng_mode)

        if (PrefsUtil.isUseActivity2(activity)) {//[61]dolphin++
            val group = findPreference(KEY_OPEN_SOURCE_GROUP) as PreferenceGroup
            val p1 = findPreference(KEY_OPEN_SOURCE_CIRCLE_IMAGE)
            if (p1 != null) {
                group.removePreference(p1)
            }
            val p2 = findPreference(KEY_CREDIT_BACKGROUND)
            if (p2 != null) {
                group.removePreference(p2)
            }
        }
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        if (mShowOpenSource) {
            when (preference.key) {
                KEY_OPEN_SOURCE_OKHTTP -> PrefsUtil.startBrowserActivity(activity,
                        "http://square.github.io/okhttp/")
                KEY_OPEN_SOURCE_STICKY_GRID -> Log.d(TAG, "FIXME: sticky grid")
                KEY_OPEN_SOURCE_CIRCLE_IMAGE -> PrefsUtil.startBrowserActivity(activity,
                        "https://github.com/markushi/android-ui")
                KEY_OPEN_SOURCE_ACTION_VIEW -> Log.d(TAG, "FIXME: action view")
                KEY_OPEN_SOURCE_SUPER_SLIM -> PrefsUtil.startBrowserActivity(activity,
                        "https://github.com/TonicArtos/SuperSLiM")
                KEY_CREDIT_BACKGROUND -> PrefsUtil.startBrowserActivity(activity,
                        "https://www.flickr.com/photos/makelessnoise/2562431372/")
                else -> Log.wtf(TAG, "onPreferenceTreeClick key == null")//should not happen
            }
            return true
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    private val TAG = "GeneralPreference"
    private val KEY_OPEN_SOURCE_GROUP = "open_source_group"
    private val KEY_OPEN_SOURCE_OKHTTP = "open_okhttp"
    private val KEY_OPEN_SOURCE_STICKY_GRID = "open_stickygridheaders"
    private val KEY_OPEN_SOURCE_CIRCLE_IMAGE = "open_markushi_android_ui"
    private val KEY_OPEN_SOURCE_ACTION_VIEW = "open_circleimageview"
    private val KEY_OPEN_SOURCE_SUPER_SLIM = "open_super_slim"
    private val KEY_CREDIT_BACKGROUND = "credit_background2"
}
