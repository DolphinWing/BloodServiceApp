@file:Suppress("PackageName")

package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import dolphin.android.apps.BloodServiceApp.R
import java.text.SimpleDateFormat
import java.util.*


/**
 * Donation activity
 *
 *
 * Created by dolphin on 2014/10/7.
 */
@Keep
class DonateActivity internal constructor(
        /**
         * Get activity title

         * @return title string
         */
        val name: String,
        /**
         * Get activity location

         * @return location string
         */
        val location: String) {
    /**
     * Get activity start time

     * @return start time
     */
    val startTime: Calendar = Calendar.getInstance(Locale.TAIWAN)
    /**
     * Get activity end time

     * @return end time
     */
    val endTime: Calendar = Calendar.getInstance(Locale.TAIWAN)

    var day: DonateDay? = null

    /**
     * Set activity start time

     * @param millis time in milliseconds
     */
    private fun setStartTime(millis: Long) {
        startTime.timeInMillis = millis
    }

    /**
     * Set activity start time

     * @param cal      reference Calendar
     * *
     * @param time_str time string
     */
    private fun setStartTime(cal: Calendar, time_str: String) {
        setStartTime(cal.timeInMillis)
        parseTime(startTime, time_str)
        //Log.d(TAG, "start: " + StartTime.getTime().toString());
    }

    /**
     * Set activity end time

     * @param millis time in milliseconds
     */
    private fun setEndTime(millis: Long) {
        endTime.timeInMillis = millis
    }

    /**
     * Set activity end time

     * @param cal      reference Calendar
     * *
     * @param time_str time string
     */
    private fun setEndTime(cal: Calendar, time_str: String) {
        setEndTime(cal.timeInMillis)
        parseTime(endTime, time_str)
        //Log.d(TAG, "end: " + EndTime.getTime().toString());
    }

    /**
     * parse time string to Calendar

     * @param cal      reference Calendar
     * *
     * @param time_str time string
     */
    private fun parseTime(cal: Calendar, time_str: String) {
        //Log.d(TAG, time_str);
        try {
            val ts = time_str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (ts.size < 2) {//try Taipei pattern
                if (time_str.matches("[0-9]+".toRegex()) && time_str.length > 3) {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str.substring(0, 2)))
                    cal.set(Calendar.MINUTE, Integer.parseInt(time_str.substring(2)))
                } else if (time_str.matches("[0-9]+".toRegex())) {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str))
                    cal.set(Calendar.MINUTE, 0)
                } else {
                    //maybe we have leading numbers
                    //time_str=17點
                    var found = false

                    val newTimeStr = time_str.replace("\\D+".toRegex(), "")
                    //Log.d(TAG, "str: " + new_time_str);
                    if (newTimeStr.matches("[0-9]+".toRegex())) {
                        if (time_str.length > 3) {
                            cal.set(Calendar.HOUR_OF_DAY,
                                    Integer.parseInt(newTimeStr.substring(0, 2)))
                            cal.set(Calendar.MINUTE, Integer.parseInt(newTimeStr.substring(2)))
                        } else {
                            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(newTimeStr))
                            cal.set(Calendar.MINUTE, 0)
                        }
                        found = true
                    }

                    if (!found) {
                        throw NumberFormatException("no time")
                    }
                }
            } else {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ts[0]))
                cal.set(Calendar.MINUTE, Integer.parseInt(ts[1]))
            }
        } catch (e: NumberFormatException) {
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            Log.e(TAG, String.format("message: %s\ntime_str: %s", e.message, time_str))
        }

        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        //Log.d(TAG, cal.getTime().toString());
    }

    /**
     * Set activity duration

     * @param cal          reference Calendar
     * *
     * @param duration_str duration string
     */
    fun setDuration(cal: Calendar, duration_str: String) {
        //Log.d(TAG, "duration_str: " + duration_str);
        var ts = duration_str.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (ts.size < 2) {//try Hsinchu pattern
            ts = duration_str.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        if (ts.size < 2) {
            ts = if (duration_str.length >= 4)
                arrayOf(duration_str.substring(0, 2), duration_str.substring(2, 4))
            else arrayOf(duration_str, "1700")
        }
        if (ts.size < 2) {
            return
        }
        //Log.d(TAG, String.format("ts[0]=%s, ts[1]=%s", ts[0], ts[1]));
        setStartTime(cal, ts[0])
        setEndTime(cal, ts[1])
    }

    /**
     * Get activity duration

     * @return duration string
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val duration: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.TAIWAN)
            return String.format("%s ~ %s",
                    sdf.format(startTime.time),
                    sdf.format(endTime.time))
        }

    val startTimeString: String
        get() = SimpleDateFormat("HH:mm", Locale.TAIWAN).format(startTime.time)

    val endTimeString: String
        get() = SimpleDateFormat("HH:mm", Locale.TAIWAN).format(endTime.time)

    /**
     * Get simple date time string

     * @param cal Calendar
     * *
     * @return time string
     */
    private fun getSimpleDateTimeString(cal: Calendar): String {
        return DonateDay.getSimpleDateTimeString(cal)
    }

    override fun toString(): String {
        return "DonateActivity{Name='$name', StartTime=${getSimpleDateTimeString(startTime)}, " +
                "EndTime=${getSimpleDateTimeString(endTime)}, Location='$location'}"
    }

    override fun equals(other: Any?): Boolean {
        if (other is DonateActivity) {
            return other.location == location && other.duration == duration && other.name == name
        }
        return false//super.equals(o);
    }

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
        return src.split(splitter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    fun prepareLocationList(context: Context): ArrayList<String> {
        Log.d(TAG, "prepare location for $name $location")

        val list = ArrayList<String>()
        list.add(name) //add name first
        //check if we should split the name
        if (name.contains("(")) {
            val name1 = name.split("(")
            list.add(name1[0])
            if (name1.size > 1 && name1[1].isNotEmpty()) {
                if (name1[1].contains(")")) {
                    list.add(splitName(name1[1], "\\)")[0])
                } else {
                    list.add(name1[1])
                }
            }
        }
        val lParen = context.getString(R.string.search_on_map_split_lparen)
        val rParen = context.getString(R.string.search_on_map_split_rparen)
        if (name.contains(lParen)) {
            val name1 = name.split(lParen)
            list.add(name1[0])
            if (name1.size > 1 && name1[1].isNotEmpty()) {
                if (name1[1].contains(rParen)) {
                    list.add(splitName(name1[1], rParen)[0])
                } else {
                    list.add(name1[1])
                }
            }
        }

        //check the location
        when {
            location.contains("　") ->
                splitName(location, "　").forEach { l ->
                    splitByParentheses1(l).forEach { l1 ->
                        list.add(removeNumberTrailing(context, l1))
                    }
                }
            location.contains("(") ->
                splitByParentheses1(location).forEach { l ->
                    list.add(removeNumberTrailing(context, l))
                }
            else ->
                splitByParentheses2(context, location).forEach { l ->
                    list.add(removeNumberTrailing(context, l))
                }
        }

//        //http://stackoverflow.com/a/203992
//        val s = LinkedHashSet(list)
//        list.clear()
//        list.addAll(s)
        return list
    }

    private fun splitByParentheses1(location: String): Array<String> {
        if (location.contains("(")) {
            val loc = splitName(location, "\\(")
            //location.split("\\(".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in loc.indices) {
                loc[i] = if (loc[i].contains(")")) loc[i].substring(0,
                        loc[i].indexOf(")")) else loc[i]
            }
            return loc
        }
        return arrayOf(location)
    }

    private fun splitByParentheses2(context: Context, location: String): Array<String> {
        val lParen = context.getString(R.string.search_on_map_split_lparen)
        val rParen = context.getString(R.string.search_on_map_split_rparen)
        if (location.contains(lParen)) {
            val loc = splitName(location, lParen)
            //location.split(lParen.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in loc.indices) {
                loc[i] = when {
                    loc[i].contains(rParen) -> loc[i].substring(0, loc[i].indexOf(rParen))
                    loc[i].contains(")") -> loc[i].substring(0, loc[i].indexOf(")"))
                    else -> loc[i]
                }
            }
            return loc
        }
        return arrayOf(location)
    }

    private fun removeNumberTrailing(context: Context, location: String): String {
        val num = context.getString(R.string.search_on_map_split_number)
        if (location.contains(num)) {
            return location.substring(0, location.indexOf(num) + 1)
        } else {
            //FIXME: maybe some other patterns?
        }
        return location
    }

    val accessibilityString: String
        get() = String.format("%s %s %s %s", day?.dateString ?: "", name, duration, location)
}
