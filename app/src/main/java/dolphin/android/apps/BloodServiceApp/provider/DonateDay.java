package dolphin.android.apps.BloodServiceApp.provider;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by dolphin on 2014/10/7.
 */
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

    public void setDate(Calendar calendar) {
        Day.setTimeInMillis(calendar.getTimeInMillis());
        Day = resetToMidNight(Day);
    }

    public long getTimeInMillis() {
        return Day.getTimeInMillis();
    }

    public boolean isFuture() {
        if (Day.after(Calendar.getInstance(Locale.TAIWAN))) {
            return true;
        } else if (DateUtils.isToday(getTimeInMillis())) {
            return true;
        }
        return false;
    }

    public static Calendar resetToMidNight(Calendar calendar) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        cal.setTimeInMillis(calendar.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public String getDateString() {
        return getSimpleDateString(Day);
    }

    public static String getSimpleDateString(Calendar cal) {
        return new SimpleDateFormat("yyyy/MM/dd (E)",
                Locale.TAIWAN).format(cal.getTime());
    }

    public static String getSimpleDateTimeString(Calendar cal) {
        return new SimpleDateFormat("MM/dd HH:mm",
                Locale.TAIWAN).format(cal.getTime());
    }

    public static String getDefaultDateString(Calendar cal) {
        return DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
    }

    public List<DonateActivity> getActivities() {
        return Activities;
    }

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
