package dolphin.android.apps.BloodServiceApp.provider

import android.util.Log
import com.google.firebase.perf.metrics.AddTrace
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Reader interface
 */
interface BloodDataReader {
    /**
     * Warmup browser and cache
     */
    fun warmUp()

    /**
     * Read html content of blood storage in each center
     *
     * @return html content
     */
    fun readBloodStorage(): String

    /**
     * Read html content of donation week calendar
     *
     * @param id blood center id
     * @param weekDay first day of the week
     * @return html content
     */
    fun readWeekCalendar(id: Int, weekDay: Calendar): String

    /**
     * Read html content of donation location list
     *
     * @param centerId blood center id
     * @param cityId city id
     * @param url city stations url
     * @return html content
     */
    fun readDonationSpotList(centerId: Int, cityId: Int, url: String): String
}

/**
 * Default implementation of providing data for parser. It will read data from website.
 *
 * @param timeout timeout in seconds
 */
open class BloodDataReaderImpl(timeout: Long = 5) : BloodDataReader {
    companion object {
        private const val TAG = "BloodDataReader"
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS) // connect timeout
        .readTimeout(timeout, TimeUnit.SECONDS) // socket timeout
        .build()

    override fun warmUp() {
        val request: Request = Request.Builder()
            .url(BloodCenter.URL_BASE_BLOOD_ORG + "/robots.txt")
            .get()
            .build()
        try {
            client.newCall(request).execute()
        } catch (e: IOException) {
            Log.e(TAG, "warm: " + e.message)
        }
    }

    protected open fun body(url: String): String {
        val request: Request = Request.Builder().url(url).build()
        val response: Response
        return try {
            response = client.newCall(request).execute()
            return response.body?.string() ?: ""
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, String.format("%s from %s", e.message, url))
            "(io)"
        } catch (e: NullPointerException) {
            Log.e(TAG, "null pointer exception")
            "(null)"
        }
    }

    @AddTrace(name = "storage")
    override fun readBloodStorage(): String {
        return body(BloodCenter.URL_BLOOD_STORAGE)
    }

    @AddTrace(name = "week_calendar")
    override fun readWeekCalendar(id: Int, weekDay: Calendar): String {
        // make sure it is Sunday
        while (weekDay[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY) {
            weekDay.add(Calendar.DAY_OF_WEEK, -1)
        }
        val day = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(weekDay.time)
        val url = BloodCenter.URL_LOCAL_BLOOD_CENTER_WEEK
            .replace("{site}", id.toString())
            .replace("{date}", day)
        return body(url)
    }

    @AddTrace(name = "spot_list")
    override fun readDonationSpotList(centerId: Int, cityId: Int, url: String): String {
        // val url = (endpoint + BloodCenter.QS_LOCATION_MAP_CITY).replace("{city}", cityId)
        return body(url)
    }
}
