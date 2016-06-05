package dolphin.android.apps.BloodServiceApp.provider;

import android.support.annotation.Keep;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Donation activities in a day.
 * <p/>
 * Created by dolphin on 2014/10/7.
 */
@Keep
public class DonateDay {
    private Calendar Day;
    private List<DonateActivity> Activities;

    public DonateDay(List<DonateActivity> list) {
        Activities = list;
        Day = Calendar.getInstance(Locale.TAIWAN);
    }

    @SuppressWarnings("ResourceType")
    public void setDate(int year, int month, int dayOfMonth) {
        //Day = Calendar.getInstance();
        if (month >= 12 || month < 0)
            throw new IllegalArgumentException("month is between 0~11");
        Day = resetToMidNight(Day);
        Day.set(year, month - 1, dayOfMonth);
    }

    /**
     * Set date
     *
     * @param calendar Calendar
     */
    public void setDate(Calendar calendar) {
        Day.setTimeInMillis(calendar.getTimeInMillis());
        Day = resetToMidNight(Day);
    }

    /**
     * Get date time in milliseconds
     *
     * @return time in milliseconds
     */
    public long getTimeInMillis() {
        return Day.getTimeInMillis();
    }

    /**
     * Indicate if the time is in the future.
     *
     * @return true if the time is in the future
     */
    public boolean isFuture() {
        if (Day.after(Calendar.getInstance(Locale.TAIWAN))) {
            return true;
        } else if (DateUtils.isToday(getTimeInMillis())) {
            return true;
        }
        return false;
    }

    /**
     * Set date time to midnight
     *
     * @param calendar Calendar
     * @return formatted time
     */
    public static Calendar resetToMidNight(Calendar calendar) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        cal.setTimeInMillis(calendar.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Get date string
     *
     * @return date string
     */
    public String getDateString() {
        return getSimpleDateString(Day);
    }

    /**
     * Get simple date string
     *
     * @param cal Calendar
     * @return date string
     */
    public static String getSimpleDateString(Calendar cal) {
        return new SimpleDateFormat("yyyy/MM/dd (E)",
                Locale.TAIWAN).format(cal.getTime());
    }

    /**
     * Get simple date and time string
     *
     * @param cal Calendar
     * @return date and time string
     */
    public static String getSimpleDateTimeString(Calendar cal) {
        return new SimpleDateFormat("MM/dd HH:mm",
                Locale.TAIWAN).format(cal.getTime());
    }

    /**
     * Get date string in default format
     *
     * @param cal Calendar
     * @return date string
     */
    public static String getDefaultDateString(Calendar cal) {
        return DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
    }

    /**
     * Get activity list
     *
     * @return activity list
     */
    public List<DonateActivity> getActivities() {
        return Activities;
    }

    /**
     * Get activity count
     *
     * @return activity count
     */
    public int getActivityCount() {
        return Activities.size();
    }

    @Override
    public String toString() {
        String str = "";
        for (DonateActivity activity : Activities) {
            str += activity.toString() + ",";
        }
        str = getActivityCount() > 0 ? str.substring(0, str.length() - 2) : "(null)";
        return "DonateDay{" +
                "Day=" + getDateString() +
                ", Activities={" + str + "}" +
                '}';
    }
}
