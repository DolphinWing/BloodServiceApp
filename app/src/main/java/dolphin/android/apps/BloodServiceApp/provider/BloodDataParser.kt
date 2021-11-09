package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import android.util.Log
import android.util.SparseArray
import androidx.core.util.isNotEmpty
import dolphin.android.apps.BloodServiceApp.R
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Locale
import java.util.regex.Pattern

class BloodDataParser(
    context: Context,
    private val reader: BloodDataReader = BloodDataReaderImpl(), // default implementation
) {
    companion object {
        private const val TAG = "BloodDataParser"
        private const val PATTERN_STORAGE = "StorageIcon([^.]+).jpg"

        // <h3>place</h3>
        // <p><strong>time: 9:00~14:00</strong></p>
        // <p><strong>holder</strong></p>
        private const val PATTERN_ACTIVITY = "<h3>([^<]+)</h3>" +
            "[^<]*<p><strong>([^<]+)</strong></p>[^<]*<p><strong>([^<]+)"

        // <option selected="selected" value="13">臺北市</option>
        private const val PATTERN_CITY_ID = "<option([ ]selected=[^ ]+)?" +
            " value=\"([\\d]+)[^>]>([^<]+)"

        // <td width='90%'><a class='font002' href='LocationMap.aspx?spotID=79&cityID=13'
        // data-ajax='false'> 公園號捐血車</a></td>
        private const val PATTERN_SPOT_INFO = "LocationMap.aspx\\?spotID=([\\d]+)" +
            "&cityID=([\\d]+)[^>]*>([^<]*)</a>"
    }

    private val center = BloodCenter(context = context)
    private val centerIds = context.resources.getIntArray(R.array.blood_center_id)
    private val bloodType = context.resources.getStringArray(R.array.blood_type_value)
    private val cityNames = SparseArray<String>()

    private var separator: String = context.getString(R.string.pattern_separator)

    private var storageCache: SparseArray<HashMap<String, Int>>? = null

    fun warmUp() {
        reader.warmUp()
    }

    /**
     * Get blood storage data
     *
     * @param forceRefresh true if we want to re-download the storage data
     * @return storage list
     */
    fun getBloodStorage(forceRefresh: Boolean = false): SparseArray<HashMap<String, Int>> {
        storageCache?.let { cache ->
            if (!forceRefresh && cache.isNotEmpty()) return cache
        }

        val start = System.currentTimeMillis()
        val cache = storageCache ?: SparseArray()
        cache.clear()

        var html = reader.readBloodStorage()
        if (html.contains("tool_blood_cube") && html.contains("tool_danger")) {
            html = html.substring(html.indexOf("tool_blood_cube"), html.indexOf("tool_danger"))
            html.split("StorageHeader").drop(1).forEachIndexed { i, storage ->
                // Log.d(TAG, String.format("site=%d", mBloodCenterId[i]));
                var j = 0
                val storageMap = HashMap<String, Int>()
                val matcher = Pattern.compile(PATTERN_STORAGE).matcher(storage)
                while (matcher.find() && j < 4) {
                    // Log.d(TAG, String.format("  id=%d", Integer.parseInt(matcher.group(1))));
                    val type: Int = try {
                        matcher.group(1)?.toInt() ?: 0
                    } catch (e: Exception) {
                        0
                    }
                    storageMap[bloodType[j++]] = type
                }
                cache.put(centerIds[i], storageMap)
            }
        }

        Log.v(TAG, String.format("END storage wasted %d ms", System.currentTimeMillis() - start))
        storageCache = cache
        return cache
    }

    /**
     * Get event list of the coming week. Usually it only contains today and later 6 days.
     *
     * @param id blood center id
     * @return event list of the coming week
     */
    fun getLatestWeekCalendar(id: Int): List<DonateDay> {
        val cal = Calendar.getInstance(Locale.TAIWAN)
        while (cal[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_WEEK, -1)
        }
        val thisWeek = getWeekCalendar(id, cal).filter { day -> day.isFuture }
        cal.add(Calendar.DAY_OF_WEEK, 7) // check next week
        val nextWeek = getWeekCalendar(id, cal).dropLast(thisWeek.size)
        return ArrayList<DonateDay>().apply {
            if (thisWeek.isNotEmpty()) addAll(thisWeek)
            if (nextWeek.isNotEmpty()) addAll(nextWeek)
        }
    }

    /**
     * Get week calendar
     *
     * @param id blood center id
     * @param refCal reference time
     * @return event list of the specific week
     */
    fun getWeekCalendar(
        id: Int,
        refCal: Calendar = Calendar.getInstance(Locale.TAIWAN),
    ): List<DonateDay> {
        var html = reader.readWeekCalendar(id, refCal)
        if (html.contains("<div id=\"calendar\">")) {
            html = html.substring(
                html.indexOf("<div id=\"calendar\">"),
                html.lastIndexOf("<div class=\"BackToTop\" id=\"toTop\">")
            )
        }
        val cal = Calendar.getInstance(Locale.TAIWAN).apply {
            timeInMillis = refCal.timeInMillis
        }
        val donateDays = ArrayList<DonateDay>()
        val start = System.currentTimeMillis()
        html.split("data-role=\"list-divider\"").drop(1).forEach { day_html ->
            val list = ArrayList<DonateActivity>()
            // <h3>place</h3>
            // <p><strong>time: 9:00~14:00</strong></p>
            // <p><strong>holder</strong></p>
            val matcher = Pattern.compile(PATTERN_ACTIVITY).matcher(day_html)
            while (matcher.find()) {
                var name = matcher.group(3)?.trim { it <= ' ' }
                if (name?.contains(separator) == true) {
                    name = name.substring(name.indexOf(separator) + 1)
                }
                var time = matcher.group(2)?.trim { it <= ' ' }
                if (time?.contains(separator) == true) {
                    time = time.substring(time.indexOf(separator) + 1)
                }

                var location = matcher.group(1)?.trim { it <= ' ' }
                if (location?.contains(separator) == true) {
                    location = location.substring(location.indexOf(separator) + 1)
                }

                val donateActivity = DonateActivity(name ?: "", location ?: "")
                time?.let { str -> donateActivity.setDuration(cal, str) }
                if (!list.contains(donateActivity)) { // [52]++
                    list.add(donateActivity)
                }
            }
            donateDays.add(DonateDay(list).apply { setDate(cal) })
            cal.add(Calendar.DAY_OF_MONTH, 1) // advance to next day
        }
        Log.v(TAG, String.format("END calendar cost %d ms", System.currentTimeMillis() - start))
        return donateDays
    }

    /**
     * Get spot list from website
     *
     * @param id blood center id
     * @return location list in each city of the blood center region
     */
    fun getDonationSpotLocationMap(id: Int): Pair<ArrayList<Int>, SparseArray<SpotList>> {
        val maps = SparseArray<SpotList>()
        val order = ArrayList<Int>()
        val start = System.currentTimeMillis()

        center.values().find { c -> c.id == id }?.let { bloodCenter ->
            bloodCenter.city().forEach { cityId ->
                order.add(cityId)

                var html = reader.readDonationSpotList(id, cityId, bloodCenter.stationsUrl(cityId))
                if (html.contains("CalendarContentRight")) {
                    html = html.substring(
                        html.indexOf("CalendarContentRight"),
                        html.indexOf("ShortCutBox")
                    )
                    if (html.contains("SelectCity")) { // cache the city name
                        parseSelectCity(html, cityId)
                    }
                    // Log.d(TAG, String.format("html: %d", html.length()));
                    if (html.contains("InnerLocation001")) {
                        val list = parseInnerLocation(html, bloodCenter.id, cityId)
                        maps.put(cityId, list)
                    }
                }
            }
        }

        Log.v(TAG, String.format("END spot list cost %d ms", System.currentTimeMillis() - start))
        return Pair(order, maps)
    }

    private fun parseSelectCity(html: String, cityId: Int) {
        val matcher = Pattern.compile(PATTERN_CITY_ID).matcher(html)
        while (matcher.find()) {
            val cid = matcher.group(2)?.trim { it <= ' ' }
            val name = matcher.group(3)?.trim { it <= ' ' } ?: cityId.toString()
            // Log.d(TAG, "id=" + id + ", " + name);
            cid?.let { c -> cityNames.put(c.toInt(), name) }
        }
    }

    private fun parseInnerLocation(html: String, centerId: Int, cityId: Int): SpotList {
        val list = SpotList(cityId.toString())
        val locations = html.split("InnerLocation001").toTypedArray()
        if (locations.isNotEmpty()) { // static locations
            parseDonationSpotHtml(centerId, list, locations[1], true)
        }
        if (locations.size > 1) { // dynamic locations
            parseDonationSpotHtml(centerId, list, locations[2], false)
        }
        list.cityName = cityNames.get(cityId)
        return list
    }

    private fun parseDonationSpotHtml(
        centerId: Int,
        list: SpotList,
        html: String,
        isStatic: Boolean = false,
    ) {
        val matcher = Pattern.compile(PATTERN_SPOT_INFO).matcher(html)
        while (matcher.find()) {
            val spotId = matcher.group(1)?.trim { it <= ' ' } ?: ""
            val cityId = matcher.group(2)?.trim { it <= ' ' } ?: ""
            val name = matcher.group(3)?.trim { it <= ' ' } ?: cityId
            // Log.d(TAG, "  spotId = " + spotId + ", " + name);
            val info = SpotInfo(spotId, cityId, name).apply {
                siteId = centerId
            }
            if (isStatic) {
                list.addStaticLocation(info)
            } else {
                list.addDynamicLocation(info)
            }
        }
    }
}
