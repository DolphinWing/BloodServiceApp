package dolphin.android.apps.BloodServiceApp.provider

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.util.PackageUtils

/**
 * Intent builder to make various Intent among the app.
 */
object IntentBuilder {
    // https://developer.chrome.com/multidevice/android/customtabs
    // https://github.com/GoogleChrome/custom-tabs-client
    const val EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION"
    const val EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR"

    @SuppressLint("ObsoleteSdkInt")
    fun makeBrowserIntent(context: Context, url: String?): Intent? {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (context.resources.getBoolean(R.bool.feature_enable_chrome_custom_tabs)) {
            val extras = Bundle() // [44]dolphin++ add Chrome Custom Tabs
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null)
            }
            extras.putInt(
                EXTRA_CUSTOM_TABS_TOOLBAR_COLOR,
                ContextCompat.getColor(context, R.color.bloody_color)
            )
            intent.putExtras(extras)
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return if (PackageUtils.isCallable(context, intent)) intent else null
    }

    private fun makeOpenBloodCalendarSourceUrl(siteId: Int): String? {
        return BloodCenter.URL_LOCAL_BLOOD_CENTER_WEEK
            .replace("{site}", siteId.toString())
            .replace("&date={date}", "") // don't specify date
    }

    /**
     * Get Intent to website
     *
     * @param context Context
     * @param siteId  site id
     * @return Intent
     */
    fun makeOpenBloodCalendarSourceIntent(context: Context, siteId: Int): Intent? {
        return makeBrowserIntent(context, makeOpenBloodCalendarSourceUrl(siteId))
    }

    private fun makeOpenSpotLocationMapUrl(info: SpotInfo?): String? {
        // "?site_id={site}&select_city={city}";
        return info?.let { si ->
            BloodCenter.URL_LOCAL_BLOOD_LOCATION_MAP
                .replace("{site}", si.siteId.toString())
                .replace("{city}", si.cityId.toString())
                .replace("{spot}", si.spotId.toString())
        }
    }

    /**
     * Get Intent to individual donation spot mobile version website
     *
     * @param context Context
     * @param info    donation spot information
     * @return Intent
     */
    fun makeOpenSpotLocationMapIntent(context: Context, info: SpotInfo?): Intent? {
        return info?.let { si -> makeBrowserIntent(context, makeOpenSpotLocationMapUrl(si)) }
    }

    private const val FACEBOOK_PACKAGE = "com.facebook.katana"
    private const val FACEBOOK_URL = "https://www.facebook.com"

    /**
     * Get Intent to Facebook app or website
     *
     * @param context Context
     * @param siteId  site id
     * @return Intent
     */
    fun makeOpenFacebookIntent(context: Context, siteId: Int): Intent? {
        // Opening facebook app on specified profile page
        // http://stackoverflow.com/a/10788387/2673859
        val ids = context.resources.getIntArray(R.array.blood_center_id)
        var i: Int = ids.size - 1
        while (i > 0) {
            if (ids[i] == siteId) {
                break
            }
            i--
        }
        val fbIds = context.resources.getStringArray(R.array.blood_center_facebook)[i]
        return try {
            // Checks if FB is even installed.
            val pInfo = context.packageManager.getPackageInfo(FACEBOOK_PACKAGE, 0)
            // try to make intent with Facebook URI
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    String.format(
                        "fb://page/%s",
                        fbIds.split(":").toTypedArray()[1]
                    )
                )
            ).apply {
                setPackage(pInfo.packageName)
                if (!PackageUtils.isCallable(context, this)) {
                    throw Exception("can't support Facebook app")
                }
            }
        } catch (e: Exception) { // catches and opens a url to the desired page
            makeBrowserIntent(
                context,
                String.format("%s/%s", FACEBOOK_URL, fbIds.split(":").toTypedArray()[0])
            )
        }
    }

    /**
     * Check if Google Chrome is installed.
     *
     * @param context Context
     * @return true if installed
     */
    fun isGoogleChromeInstalled(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(BloodCenter.URL_BASE_BLOOD_ORG)
        )
        val list = context.packageManager.queryIntentActivities(intent, 0)
        if (list.size > 0) {
            for (resolveInfo in list) {
                // Log.d("CpblCalendarHelper", resolveInfo.activityInfo.packageName);
                if (resolveInfo.activityInfo.packageName.startsWith("com.android.chrome")) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * http://wp.me/p2XxfD-1u
     *
     * @return true if installed
     */
    fun isGoogleMapsInstalled(context: Context?): Boolean {
        return if (context == null) {
            false
        } else try {
            context.packageManager
                .getApplicationInfo("com.google.android.apps.maps", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}