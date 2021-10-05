package dolphin.android.apps.BloodServiceApp

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo

/**
 * Intent helper class to deal with Calendar and Maps
 */
object IntentHelper {
    /**
     * https://developer.android.com/guide/topics/providers/calendar-provider#intents
     *
     * @param context Android Context
     * @param event donation event
     */
    fun addToCalendar(context: Context, event: DonateActivity) {
        val calIntent = Intent(Intent.ACTION_INSERT).apply {
            // setDataAndType(CalendarContract.Events.CONTENT_URI, "vnd.android.cursor.item/event")
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.name)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                context.getString(R.string.action_add_to_calendar_description)
            )
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTime.timeInMillis)
        }
        context.startActivity(calIntent)
    }

    /**
     * Search on Google Maps. First we will show a list for user to decide where to search because
     * the location name may contains various information. Split them into a list and let user
     * choose from one of them.
     *
     * @param activity Android Activity
     * @param event donation event
     */
    fun searchOnMap(activity: AppCompatActivity, event: DonateActivity) {
        val list = event.prepareLocationList(activity).toTypedArray()
        AlertDialog.Builder(activity)
            .setTitle(R.string.action_search_on_maps)
            .setCancelable(true)
            .setItems(list) { _, index ->
                // Log.d(TAG, "select $index ${list[index]}")
                if (PrefsUtil.isGoogleMapsInstalled(activity)) {
                    openActivityOnGoogleMaps(activity, list[index])
                } else {
                    openActivityOnGoogleMapsInBrowser(activity, list[index])
                }
            }
            .show()
    }

    private fun openActivityOnGoogleMaps(context: Context, location: String) {
        val mapIntent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=$location".toUri())
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    private fun openActivityOnGoogleMapsInBrowser(context: Context, location: String) {
        // https://www.google.com/maps/search/?api=1&query=centurylink+field
        val mapIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.google.com/maps/search/?api=1&query=$location".toUri()
        )
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    /**
     * Show spot info in a browser
     *
     * @param context Android Context
     * @param info target donation spot info
     */
    fun showSpotInfo(context: Context, info: SpotInfo) {
        BloodDataHelper.getOpenSpotLocationMapIntent(context, info)?.let { intent ->
            context.startActivity(intent) //show in browser, don't parse it
        }
    }

    /**
     * Show Facebook Pages
     *
     * @param context Android Context
     * @param id target blood center id
     */
    fun showBloodCenterFacebookPages(context: Context, id: Int) {
        BloodDataHelper.getOpenFacebookIntent(context, id)?.let { intent ->
            context.startActivity(intent)
        }
    }

    /**
     * Show Facebook Pages
     *
     * @param context Android Context
     * @param id target blood center id
     */
    fun showBloodCenterSource(context: Context, id: Int) {
        BloodDataHelper.getOpenBloodCalendarSourceIntent(context, id)?.let { intent ->
            context.startActivity(intent)
        }
    }

    /**
     * Show main blood center homepage.
     *
     * @param context Android Context
     */
    fun showMainSource(context: Context) {
        PrefsUtil.startBrowserActivity(
            context,
            Firebase.remoteConfig.getString("url_blood_center_main")
        )
    }

    /**
     * Show donor info in a browser.
     *
     * @param context Android Context
     */
    fun showDonorInfo(context: Context) {
        PrefsUtil.startBrowserActivity(
            context,
            Firebase.remoteConfig.getString("url_blood_donor_info")
        )
    }
}