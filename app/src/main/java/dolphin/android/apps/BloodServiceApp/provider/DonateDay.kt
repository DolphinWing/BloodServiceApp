package dolphin.android.apps.BloodServiceApp.provider

import android.text.format.DateUtils
import androidx.annotation.Keep
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Donation activities in a day.
 *
 * Created by dolphin on 2014/10/7.
 */
@Keep
class DonateDay(
        /**
         * Get activity list

         * @return activity list
         */
        val activities: List<DonateActivity>) {
    private var day: Calendar? = null

    init {
        day = Calendar.getInstance(Locale.TAIWAN)
        activities.forEach { it.day = this }
    }

    @Suppress("unused")
    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        //day = Calendar.getInstance();
        if (month >= 12 || month < 0)
            throw IllegalArgumentException("month is between 0~11")
        day = resetToMidNight(day!!)
        day!!.set(year, month - 1, dayOfMonth)
    }

    /**
     * Set date

     * @param calendar Calendar
     */
    fun setDate(calendar: Calendar) {
        day!!.timeInMillis = calendar.timeInMillis
        day = resetToMidNight(day!!)
    }

    /**
     * Get date time in milliseconds

     * @return time in milliseconds
     */
    val timeInMillis: Long
        get() = day!!.timeInMillis

    /**
     * Indicate if the time is in the future.

     * @return true if the time is in the future
     */
    val isFuture: Boolean
        get() {
            if (day!!.after(Calendar.getInstance(Locale.TAIWAN))) {
                return true
            } else if (DateUtils.isToday(timeInMillis)) {
                return true
            }
            return false
        }

    /**
     * Get date string

     * @return date string
     */
    val dateString: String
        get() = getSimpleDateString(day!!)

    /**
     * Get activity count

     * @return activity count
     */
    val activityCount: Int
        get() = activities.size

    override fun toString(): String {
        var str = StringBuilder()
        for (activity in activities) {
            str.append(activity.toString()).append(",")
        }
        str = StringBuilder(if (activityCount > 0) str.substring(0, str.length - 2) else "(null)")
        return "DonateDay{day=$dateString,list={$str}}"
    }

    companion object {

        /**
         * Set date time to midnight

         * @param calendar Calendar
         * *
         * @return formatted time
         */
        fun resetToMidNight(calendar: Calendar): Calendar {
            val cal = Calendar.getInstance(Locale.TAIWAN)
            cal.timeInMillis = calendar.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal
        }

        /**
         * Get simple date string

         * @param cal Calendar
         * *
         * @return date string
         */
        fun getSimpleDateString(cal: Calendar): String {
            return SimpleDateFormat("yyyy/MM/dd (E)", Locale.TAIWAN).format(cal.time)
        }

        /**
         * Get simple date and time string

         * @param cal Calendar
         * *
         * @return date and time string
         */
        fun getSimpleDateTimeString(cal: Calendar): String {
            return SimpleDateFormat("MM/dd HH:mm", Locale.TAIWAN).format(cal.time)
        }

        @Suppress("unused")
                /**
                 * Get date string in default format

                 * @param cal Calendar
                 * *
                 * @return date string
                 */
        fun getDefaultDateString(cal: Calendar): String {
            return DateFormat.getDateInstance(DateFormat.FULL).format(cal.time)
        }
    }
}
