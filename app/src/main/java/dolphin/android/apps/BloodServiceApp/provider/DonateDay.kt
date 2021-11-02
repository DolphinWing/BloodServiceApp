package dolphin.android.apps.BloodServiceApp.provider

import android.text.format.DateUtils
import androidx.annotation.Keep
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Donation activities in a day.
 */
@Keep
class DonateDay(
    /**
     * activity list of today
     */
    val activities: List<DonateActivity>,
) {
    private val day: Calendar = Calendar.getInstance(Locale.TAIWAN)

    init {
        activities.forEach { a -> a.day = this }
    }

    @Suppress("unused")
    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        // day = Calendar.getInstance();
        if (month >= 12 || month < 0)
            throw IllegalArgumentException("month is between 0~11")
        resetToMidNight(day)
        day.set(year, month - 1, dayOfMonth)
    }

    /**
     * Set date
     *
     * @param calendar Calendar
     */
    fun setDate(calendar: Calendar) {
        day.timeInMillis = calendar.timeInMillis
        resetToMidNight(day)
    }

    /**
     * date time in milliseconds
     */
    val timeInMillis: Long
        get() = day.timeInMillis

    /**
     * Indicate if the time is in the future.
     *
     * @return true if the time is in the future
     */
    val isFuture: Boolean
        get() = when {
            day.after(Calendar.getInstance(Locale.TAIWAN)) -> true
            DateUtils.isToday(timeInMillis) -> true
            else -> false
        }

    /**
     * date string
     */
    val dateString: String
        get() = getSimpleDateString(day)

    /**
     * activity count
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
         *
         * @param calendar Calendar
         * @return formatted time
         */
        fun resetToMidNight(calendar: Calendar): Calendar {
            val cal = Calendar.getInstance(Locale.TAIWAN)
            cal.timeInMillis = calendar.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis = cal.timeInMillis // set back to original calendar
            return cal
        }

        /**
         * Get simple date string
         *
         * @param cal Calendar
         * @return date string
         */
        fun getSimpleDateString(cal: Calendar): String {
            return SimpleDateFormat("yyyy/MM/dd (E)", Locale.TAIWAN).format(cal.time)
        }

        /**
         * Get simple date and time string
         *
         * @param cal Calendar
         * @return date and time string
         */
        fun getSimpleDateTimeString(cal: Calendar): String {
            return SimpleDateFormat("MM/dd HH:mm", Locale.TAIWAN).format(cal.time)
        }

        /**
         * Get date string in default format
         *
         * @param cal Calendar
         * @return date string
         */
        @Suppress("unused")
        fun getDefaultDateString(cal: Calendar): String {
            return DateFormat.getDateInstance(DateFormat.FULL).format(cal.time)
        }
    }
}
