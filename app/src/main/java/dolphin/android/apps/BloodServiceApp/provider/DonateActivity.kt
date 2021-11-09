package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import dolphin.android.apps.BloodServiceApp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Donation activity
 */
@Keep
class DonateActivity constructor(
    /**
     * activity title
     */
    val name: String,

    /**
     * activity location
     */
    val location: String,
) {
    /**
     * activity start time
     */
    val startTime: Calendar = Calendar.getInstance(Locale.TAIWAN)

    /**
     * activity end time
     */
    val endTime: Calendar = Calendar.getInstance(Locale.TAIWAN)

    init {
        val now = Calendar.getInstance(Locale.TAIWAN)
        setStartTime(now)
        setEndTime(now)
    }

    /**
     * Set activity start time
     *
     * @param cal      reference Calendar
     * @param time_str time string
     */
    private fun setStartTime(cal: Calendar, time_str: String? = null) {
        startTime.timeInMillis = cal.timeInMillis
        time_str?.let { time -> parseTime(startTime, time) }
    }

    /**
     * Set activity end time
     *
     * @param cal      reference Calendar
     * @param time_str time string
     */
    private fun setEndTime(cal: Calendar, time_str: String? = null) {
        endTime.timeInMillis = cal.timeInMillis
        time_str?.let { time -> parseTime(endTime, time) }
    }

    /**
     * parse time string to Calendar
     *
     * @param cal      reference Calendar
     * @param time_str time string
     */
    private fun parseTime(cal: Calendar, time_str: String) {
        // Log.d(TAG, time_str);
        try {
            val ts = time_str.split(":").dropLastWhile { it.isEmpty() }
            if (ts.size < 2) { // try Taipei pattern
                if (time_str.matches("[0-9]+".toRegex()) && time_str.length > 3) {
                    cal.set(Calendar.HOUR_OF_DAY, time_str.substring(0, 2).parseInt())
                    cal.set(Calendar.MINUTE, time_str.substring(2).parseInt())
                } else if (time_str.matches("[0-9]+".toRegex())) {
                    cal.set(Calendar.HOUR_OF_DAY, time_str.parseInt())
                    cal.set(Calendar.MINUTE, 0)
                } else {
                    // maybe we have leading numbers
                    // time_str=17點
                    var found = false

                    val newTimeStr = time_str.replace("\\D+".toRegex(), "")
                    // Log.d(TAG, "str: " + new_time_str);
                    if (newTimeStr.matches("[0-9]+".toRegex())) {
                        if (time_str.length > 3) {
                            cal.set(Calendar.HOUR_OF_DAY, newTimeStr.substring(0, 2).parseInt())
                            cal.set(Calendar.MINUTE, newTimeStr.substring(2).parseInt())
                        } else {
                            cal.set(Calendar.HOUR_OF_DAY, newTimeStr.parseInt())
                            cal.set(Calendar.MINUTE, 0)
                        }
                        found = true
                    }

                    if (!found) {
                        throw NumberFormatException("no time")
                    }
                }
            } else {
                cal.set(Calendar.HOUR_OF_DAY, ts[0].parseInt())
                cal.set(Calendar.MINUTE, ts[1].parseInt())
            }
        } catch (e: NumberFormatException) {
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            Log.e(TAG, String.format("message: %s\ntime_str: %s", e.message, time_str))
        }

        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Log.d(TAG, cal.getTime().toString());
    }

    private fun String.parseInt(): Int = try {
        this.toInt()
    } catch (e: NumberFormatException) {
        0
    }

    /**
     * Set activity duration
     *
     * @param cal          reference Calendar
     * @param duration_str duration string
     */
    fun setDuration(cal: Calendar, duration_str: String) {
        // Log.d(TAG, "duration_str: " + duration_str);
        var ts = duration_str.split("~").dropLastWhile { it.isEmpty() }
        if (ts.size < 2) { // try Hsinchu pattern
            ts = duration_str.split("-").dropLastWhile { it.isEmpty() }
        }
        if (ts.size < 2) {
            ts = if (duration_str.length > 4)
                listOf(duration_str.substring(0, 4), duration_str.substring(4))
            else listOf(duration_str, "1700")
        }
        if (ts.size < 2) {
            return
        }
        // Log.d(TAG, String.format("ts[0]=%s, ts[1]=%s", ts[0], ts[1]));
        setStartTime(cal, ts[0])
        setEndTime(cal, ts[1])
    }

    /**
     * activity duration
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val duration: String
        get() = String.format("%s ~ %s", startTimeString, endTimeString)

    /**
     * start time string
     */
    val startTimeString: String
        get() = getSimpleTimeString(startTime)

    /**
     * end time string
     */
    val endTimeString: String
        get() = getSimpleTimeString(endTime)

    /**
     * Get simple time string
     *
     * @param cal Calendar
     * @return time string
     */
    private fun getSimpleTimeString(cal: Calendar): String { // MM/dd HH:mm
        return SimpleDateFormat("HH:mm", Locale.TAIWAN).format(cal.time)
    }

    /**
     * @suppress {@inheritDoc}
     */
    override fun toString(): String {
        return "{Name='$name', $startTimeString~$endTimeString, Location='$location'}"
    }

    /**
     * @suppress {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (other is DonateActivity) {
            return other.location == location && other.duration == duration && other.name == name
        }
        return false // super.equals(o);
    }

    /**
     * @suppress {@inheritDoc}
     */
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        return result
    }

    companion object {
        private const val TAG = "DonateActivity"
    }

    private fun splitName(src: String, splitter: String): Array<String> {
        return src.split(splitter).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * Prepare location list for search on maps
     *
     * @param context Android Context
     * @return location list
     */
    fun prepareLocationList(context: Context): ArrayList<String> {
        // Log.d(TAG, "prepare location for $name $location")

        val list = ArrayList<String>()
        list.add(name) // add name first
        // check if we should split the name
        arrayOf(
            Pair("(", ")"),
            Pair(
                context.getString(R.string.search_on_map_split_lparen),
                context.getString(R.string.search_on_map_split_rparen),
            ),
        ).filter { (lparen, rparen) ->
            name.contains(lparen) && name.contains(rparen)
        }.forEach { (lparen, rparen) ->
            val name1 = name.split(lparen)
            list.add(name1[0])
            if (name1.size > 1 && name1[1].isNotEmpty()) {
                list.add(splitName(name1[1], rparen)[0])
            }
        }

        // check the location itself
        when {
//            location.contains("　") ->
//                splitName(location, "　").forEach { loc ->
//                    splitBy("(", ")", loc).forEach { l1 ->
//                        list.add(removeNumberTrailing(context, l1))
//                    }
//                }
            location.contains("(") ->
                splitBy("(", ")", location).forEach { l ->
                    list.add(removeNumberTrailing(context, l))
                }
            else ->
                splitBy(
                    context.getString(R.string.search_on_map_split_lparen),
                    context.getString(R.string.search_on_map_split_rparen),
                    location
                ).forEach { l ->
                    list.add(removeNumberTrailing(context, l))
                }
        }
        return list
    }

    private fun splitBy(lparen: String, rparen: String, location: String): Array<String> {
        if (location.contains(lparen) && location.contains(rparen)) {
            val result = splitName(location, lparen)
            result.forEachIndexed { index, loc ->
                result[index] =
                    if (loc.contains(rparen)) loc.substring(0, loc.indexOf(rparen)) else loc
            }
            return result
        }
        return arrayOf(location)
    }

    private fun removeNumberTrailing(context: Context, location: String): String {
        val num = context.getString(R.string.search_on_map_split_number)
        var result = if (location.contains(num)) {
            location.substring(0, location.indexOf(num) + 1)
        } else {
            location
        }
        // remove some other descriptors
        arrayOf(
            context.getString(R.string.search_on_map_split_in_front),
            context.getString(R.string.search_on_map_split_across),
            context.getString(R.string.search_on_map_split_aside),
            context.getString(R.string.search_on_map_split_inside),
        ).filter { text -> location.endsWith(text) }.forEach { text ->
            result = result.dropLast(text.length)
        }
        return result
    }
}
