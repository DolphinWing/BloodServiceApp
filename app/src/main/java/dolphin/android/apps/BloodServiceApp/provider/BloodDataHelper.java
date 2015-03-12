package dolphin.android.apps.BloodServiceApp.provider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dolphin.android.apps.BloodServiceApp.R;

/**
 * Created by dolphin on 2014/10/6.
 */
public class BloodDataHelper {
    private final static String TAG = "BloodDataHelper";

    private final static String URL_BASE_BLOOD_ORG = "http://www.blood.org.tw";
    private final static String URL_BLOOD_STORAGE =
            //URL_BASE_BLOOD_ORG + "/Internet/english/index.aspx";
            URL_BASE_BLOOD_ORG + "/Internet/main/index.aspx";
    private final static String URL_LOCAL_BLOOD_CENTER_WEEK =
            URL_BASE_BLOOD_ORG + "/Internet/mobile/docs/local_blood_center_week.aspx?" +
                    "site_id={site}&date={date}";//yyyy/MM/dd

    private Context mContext;
    private OkHttpClient mClient;
    private Calendar mStartDate;

    private int[] mBloodCenterId;// = {0, 2, 3, 4, 5, 6, 7};
    private String[] mBloodType;// = {"A", "B", "O", "AB"};

    public BloodDataHelper(Context context) {
        mContext = context;
        mClient = new OkHttpClient();
        mClient.setReadTimeout(10, TimeUnit.SECONDS);
        mStartDate = Calendar.getInstance(Locale.TAIWAN);

        mBloodCenterId = mContext.getResources().getIntArray(R.array.blood_center_id);
        mBloodType = mContext.getResources().getStringArray(R.array.blood_type_value);
    }

    private String getBody(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            //Log.d(TAG, String.format("read timeout=%d", mClient.getReadTimeout()));
            response = mClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, String.format("%s from %s", e.getMessage(), url));
        }
        return "(empty)";
    }

    public ArrayList<DonateDay> getLatestWeekCalendar(int siteID) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }
        ArrayList<DonateDay> week1 = getWeekCalendar(cal, String.valueOf(siteID));
        cal.add(Calendar.DAY_OF_WEEK, 7);
        ArrayList<DonateDay> week2 = getWeekCalendar(cal, String.valueOf(siteID));
        ArrayList<DonateDay> days = new ArrayList<DonateDay>();
        for (int i = 0; i < week1.size(); i++) {
            if (week1.get(i).isFuture()) {
                days.add(week1.get(i));
            }
        }
        for (int i = 0; i < week2.size() && days.size() < 7; i++) {
            days.add(week2.get(i));
        }
        return days;
    }

    /*  <h3>place</h3>
        <p><strong>time: 9:00~14:00</strong></p>
		<p><strong>holder</strong></p>*/
    private final static String PATTERN_ACTIVITY = "<h3>([^<]+)</h3>" +
            "[^<]*<p><strong>([^<]+)</strong></p>[^<]*<p><strong>([^<]+)";

    public ArrayList<DonateDay> getWeekCalendar(Calendar calendar, String site_id) {
        mStartDate.setTimeInMillis(calendar.getTimeInMillis());
        //Log.d(TAG, mStartDate.getTime().toString());
        ArrayList<DonateDay> donateDays = new ArrayList<DonateDay>();

        String day = new SimpleDateFormat("yyyy/MM/dd").format(mStartDate.getTime());
        String url = URL_LOCAL_BLOOD_CENTER_WEEK.replace("{site}", site_id).replace("{date}", day);
        Log.v(TAG, String.format("started with site=%s, day=%s", site_id, day));

        long startTime = System.currentTimeMillis();

        String html = getBody(url);
        Log.v(TAG, String.format("get action wasted %d ms", ((System.currentTimeMillis() - startTime))));

        //Log.d(TAG, String.format("html length = %d", html.length()));
        if (html.contains("<div id=\"calendar\">")) {
            html = html.substring(html.indexOf("<div id=\"calendar\">"),
                    html.lastIndexOf("<div class=\"BackToTop\" id=\"toTop\">"));
            //Log.d(TAG, String.format("html size = %d", html.length()));
            String[] day_html = html.split("data-role=\"list-divider\"");
            //Log.d(TAG, String.format("total = %d days", day_html.length - 1));

            String separator = mContext.getString(R.string.pattern_separator);
            Calendar cal = mStartDate;
            for (int i = 1; i < day_html.length; i++, cal.add(Calendar.DAY_OF_MONTH, 1)) {
                ArrayList<DonateActivity> list = new ArrayList<DonateActivity>();
                    /*  <h3>place</h3>
                        <p><strong>time: 9:00~14:00</strong></p>
				        <p><strong>holder</strong></p>*/
                Matcher matcher = Pattern.compile(PATTERN_ACTIVITY).matcher(day_html[i]);
                while (matcher.find()) {
                    String name = matcher.group(3).trim();
                    if (name.contains(separator)) {
                        name = name.substring(name.indexOf(separator) + 1);
                    }
                    String time = matcher.group(2).trim();
                    if (time.contains(separator)) {
                        time = time.substring(time.indexOf(separator) + 1);
                    }
                    String location = matcher.group(1).trim();
                    if (location.contains(separator)) {
                        location = location.substring(location.indexOf(separator) + 1);
                    }
                    //Log.d(TAG, String.format("    name: %s", name));
                    //Log.d(TAG, String.format(" time: %s", time));
                    //Log.d(TAG, String.format("location: %s", location));
                    list.add(new DonateActivity(name, location));
                    list.get(list.size() - 1).setDuration(cal, time);
                    //Log.d(TAG, list.get(list.size() - 1).toString());
                }
                //Log.d(TAG, String.format("===> %s: %d", cal.getTime().toString(), list.size()));
                donateDays.add(new DonateDay(list));
                donateDays.get(donateDays.size() - 1).setDate(cal);
            }
        }

        long endTime = System.currentTimeMillis();
        Log.v(TAG, String.format("end with site=%s, day=%s, wasted %d ms",
                site_id, day, ((endTime - startTime))));

        return donateDays;
    }

    /* <div class='StorageCondition'><img src='images/StorageIcon003.jpg' */
    private final static String PATTERN_STORAGE = "StorageIcon([^.]+).jpg";
    private SparseArray<HashMap<String, Integer>> mBloodStorage;

    public SparseArray<HashMap<String, Integer>> getBloodStorage() {
        return getBloodStorage(true);
    }

    public SparseArray<HashMap<String, Integer>> getBloodStorage(boolean forceRefresh) {
        if (!forceRefresh && mBloodStorage != null && mBloodStorage.size() > 0) {
            return mBloodStorage;
        }

        long startTime = System.currentTimeMillis();

        if (mBloodStorage == null) {
            mBloodStorage = new SparseArray<>();
        } else {
            mBloodStorage.clear();
        }

        String html = getBody(URL_BLOOD_STORAGE);
        Log.v(TAG, String.format("get storage wasted %d ms",
                ((System.currentTimeMillis() - startTime))));

        //StorageBoard.jpg
        if (html.contains("tool_blood_cube") && html.contains("tool_danger")) {
            html = html.substring(html.indexOf("tool_blood_cube"), html.indexOf("tool_danger"));
            String[] storages = html.split("StorageHeader");
            for (int i = 1; i < storages.length; i++) {
                //Log.d(TAG, String.format("site=%d", mBloodCenterId[i]));
                int j = 0;
                HashMap<String, Integer> storageMap = new HashMap<String, Integer>();
                Matcher matcher = Pattern.compile(PATTERN_STORAGE).matcher(storages[i]);
                while (matcher.find() && j < 4) {
                    //Log.d(TAG, String.format("  id=%d", Integer.parseInt(matcher.group(1))));
                    storageMap.put(mBloodType[j++], Integer.parseInt(matcher.group(1)));
                }
                mBloodStorage.put(mBloodCenterId[i], storageMap);
            }
        } else {
            Log.w(TAG, "no storage data?");
        }

        Log.v(TAG, String.format("end storage wasted %d ms",
                ((System.currentTimeMillis() - startTime))));
        return mBloodStorage;
    }

    public String getBloodCenterName(int siteID) {
        int i = 0;
        for (i = mBloodCenterId.length - 1; i > 0; i--) {
            if (mBloodCenterId[i] == siteID) {
                break;
            }
        }
        return mContext.getResources().getStringArray(R.array.blood_center)[i];
    }

    private final static String FACEBOOK_PACKAGE = "com.facebook.katana";
    private final static String FACEBOOK_URL = "https://www.facebook.com";

    //Opening facebook app on specified profile page
    //http://stackoverflow.com/a/10788387/2673859
    public static Intent getOpenFacebookIntent(Context context, int siteId) {
        final int[] Ids = context.getResources().getIntArray(R.array.blood_center_id);
        int i;
        for (i = Ids.length - 1; i > 0; i--) {
            if (Ids[i] == siteId) {
                break;
            }
        }
        final String fbIds = context.getResources().getStringArray(R.array.blood_center_facebook)[i];
        Intent intent;
        try {
            //Checks if FB is even installed.
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(FACEBOOK_PACKAGE, 0);
            //Trys to make intent with FB's URI
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format("fb://page/%s", fbIds.split(":")[1])));
            intent.setPackage(pInfo.packageName);
        } catch (Exception e) {//catches and opens a url to the desired page
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format("%s/%s", FACEBOOK_URL, fbIds.split(":")[0])));
        }
        return intent;
    }
}
