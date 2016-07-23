package dolphin.android.apps.BloodServiceApp.provider;

import android.content.Context;
import android.support.annotation.Keep;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Donation activity
 * <p/>
 * Created by dolphin on 2014/10/7.
 */
@Keep
public class DonateActivity {
    private String Name;
    private Calendar StartTime;
    private Calendar EndTime;
    private String Location;

    public DonateActivity(String name, String location) {
        Name = name;
        Location = location;
        StartTime = Calendar.getInstance(Locale.TAIWAN);
        EndTime = Calendar.getInstance(Locale.TAIWAN);
    }

    /**
     * Get activity title
     *
     * @return title string
     */
    public String getName() {
        return Name;
    }

    /**
     * Get activity location
     *
     * @return location string
     */
    public String getLocation() {
        return Location;
    }

    /**
     * Get activity start time
     *
     * @return start time
     */
    public Calendar getStartTime() {
        return StartTime;
    }

    /**
     * Set activity start time
     *
     * @param millis time in milliseconds
     */
    public void setStartTime(long millis) {
        StartTime.setTimeInMillis(millis);
    }

    /**
     * Set activity start time
     *
     * @param cal      reference Calendar
     * @param time_str time string
     */
    public void setStartTime(Calendar cal, String time_str) {
        setStartTime(cal.getTimeInMillis());
        parseTime(StartTime, time_str);
        //Log.d("BloodDataHelper", "start: " + StartTime.getTime().toString());
    }

    /**
     * Get activity end time
     *
     * @return end time
     */
    public Calendar getEndTime() {
        return EndTime;
    }

    /**
     * Set activity end time
     *
     * @param millis time in milliseconds
     */
    public void setEndTime(long millis) {
        EndTime.setTimeInMillis(millis);
    }

    /**
     * Set activity end time
     *
     * @param cal      reference Calendar
     * @param time_str time string
     */
    public void setEndTime(Calendar cal, String time_str) {
        setEndTime(cal.getTimeInMillis());
        parseTime(EndTime, time_str);
        //Log.d("BloodDataHelper", "end: " + EndTime.getTime().toString());
    }

    /**
     * parse time string to Calendar
     *
     * @param cal      reference Calendar
     * @param time_str time string
     */
    private void parseTime(Calendar cal, String time_str) {
        //Log.d("BloodDataHelper", time_str);
        try {
            String[] ts = time_str.split(":");
            if (ts.length < 2) {//try Taipei pattern
                if (time_str.matches("[0-9]+") && time_str.length() > 3) {
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str.substring(0, 2)));
                    cal.set(Calendar.MINUTE, Integer.parseInt(time_str.substring(2)));
                } else {
                    throw new NumberFormatException("no time");
                }
            } else {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(ts[1]));
            }
        } catch (NumberFormatException e) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            Log.e("BloodDataHelper", String.format("message: %s\ntime_str: %s",
                e.getMessage(), time_str));
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //Log.d("BloodDataHelper", cal.getTime().toString());
    }

    /**
     * Set activity duration
     *
     * @param cal          reference Calendar
     * @param duration_str duration string
     */
    public void setDuration(Calendar cal, String duration_str) {
        String[] ts = duration_str.split("~");
        if (ts.length < 2) {//try Hsinchu pattern
            ts = duration_str.split("-");
        }
        if (ts.length < 2 && duration_str.length() >= 4) {//no separator
            ts = new String[2];
            ts[0] = duration_str.substring(0, 2);
            ts[1] = duration_str.substring(2, 4);
        }
        if (ts.length < 2) {
            return;
        }
        setStartTime(cal, ts[0]);
        setEndTime(cal, ts[1]);
    }

    /**
     * Get activity duration
     *
     * @return duration string
     */
    public String getDuration() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.TAIWAN);
        //Log.d("BloodDataHelper", "start: " + StartTime.getTime().toString());
        //Log.d("BloodDataHelper", "  end: " + EndTime.getTime().toString());
        return String.format("%s ~ %s",
                sdf.format(StartTime.getTime()),
                sdf.format(EndTime.getTime()));
    }

    /**
     * Get activity duration
     *
     * @param context Context
     * @return duration string by system settings
     */
    public String getDuration(Context context) {
        if (DateFormat.is24HourFormat(context)) {
            return getDuration();
        }
        java.text.DateFormat timeFormatter = DateFormat.getTimeFormat(context);
        return String.format("%s ~ %s",
                timeFormatter.format(StartTime.getTime()),
                timeFormatter.format(EndTime.getTime()));
    }

    /**
     * Get simple date time string
     *
     * @param cal Calendar
     * @return time string
     */
    private String getSimpleDateTimeString(Calendar cal) {
        return DonateDay.getSimpleDateTimeString(cal);
    }

    @Override
    public String toString() {
        return "DonateActivity{" +
                "Name='" + Name + '\'' +
                ", StartTime=" + getSimpleDateTimeString(StartTime) +
                ", EndTime=" + getSimpleDateTimeString(EndTime) +
                ", Location='" + Location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DonateActivity) {
            DonateActivity activity = (DonateActivity) o;
            return activity.getLocation().equals(getLocation())
                    && activity.getDuration().equals(getDuration())
                    && activity.getName().equals(getName());
        }
        return false;//super.equals(o);
    }
}
