package dolphin.android.apps.BloodServiceApp.provider;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by dolphin on 2014/10/7.
 */
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

    public String getName() {
        return Name;
    }

    public String getLocation() {
        return Location;
    }

    public Calendar getStartTime() {
        return StartTime;
    }

    public void setStartTime(long millis) {
        StartTime.setTimeInMillis(millis);
    }

    public void setStartTime(Calendar cal, String time_str) {
        setStartTime(cal.getTimeInMillis());
        parseTime(StartTime, time_str);
        //Log.d("BloodDataHelper", "start: " + StartTime.getTime().toString());
    }

    public Calendar getEndTime() {
        return EndTime;
    }

    public void setEndTime(long millis) {
        EndTime.setTimeInMillis(millis);
    }

    public void setEndTime(Calendar cal, String time_str) {
        setEndTime(cal.getTimeInMillis());
        parseTime(EndTime, time_str);
        //Log.d("BloodDataHelper", "end: " + EndTime.getTime().toString());
    }

    private void parseTime(Calendar cal, String time_str) {
        //Log.d("BloodDataHelper", time_str);
        try {
            String[] ts = time_str.split(":");
            if (ts.length < 2) {//try Taipei pattern
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_str.substring(0, 2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(time_str.substring(2)));
            } else {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(ts[1]));
            }
        } catch (NumberFormatException e) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            Log.e("BloodDataHelper", time_str);
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //Log.d("BloodDataHelper", cal.getTime().toString());
    }

    public void setDuration(Calendar cal, String duration_str) {
        String[] ts = duration_str.split("~");
        if (ts.length < 2) {//try Hsinchu pattern
            ts = duration_str.split("-");
        }
        setStartTime(cal, ts[0]);
        setEndTime(cal, ts[1]);
    }

    public String getDuration() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.TAIWAN);
        //Log.d("BloodDataHelper", "start: " + StartTime.getTime().toString());
        //Log.d("BloodDataHelper", "  end: " + EndTime.getTime().toString());
        return String.format("%s ~ %s",
                sdf.format(StartTime.getTime()),
                sdf.format(EndTime.getTime()));
    }

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
}
