package dolphin.android.apps.BloodServiceApp.provider

import android.text.format.DateUtils
import androidx.annotation.Keep
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

    @Suppress("unused")
    fun setDate(year: Int, monthOfJava: Int, dayOfMonth: Int) {
        // day = Calendar.getInstance();
        if (monthOfJava >= 12 || monthOfJava < 0)
            throw IllegalArgumentException("month is between 0~11")
        day.reset()
        day.set(year, monthOfJava, dayOfMonth)
    }

    /**
     * Set date
     *
     * @param calendar Calendar
     */
    fun setDate(calendar: Calendar) {
        day.timeInMillis = calendar.timeInMillis
        day.reset()
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
        get() = SimpleDateFormat("yyyy/MM/dd (E)", Locale.TAIWAN).format(day.time)

    /**
     * activity count
     */
    val activityCount: Int
        get() = activities.size

    /**
     * @suppress {@inheritDoc}
     */
    override fun toString(): String {
        var str = StringBuilder()
        for (activity in activities) {
            str.append(activity.toString()).append(",")
        }
        str = StringBuilder(if (activityCount > 0) str.substring(0, str.length - 2) else "(null)")
        return "DonateDay{day=$dateString,list={$str}}"
    }
}

/**
 * Set date time to midnight of the day
 *
 * @return formatted time
 */
fun Calendar.reset(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}
