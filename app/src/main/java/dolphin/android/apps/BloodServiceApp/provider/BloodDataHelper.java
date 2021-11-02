package dolphin.android.apps.BloodServiceApp.provider;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dolphin on 2014/10/6.
 * <p/>
 * Data parsing helper
 */
public class BloodDataHelper {
    private final static String TAG = "BloodDataHelper";
    private final static boolean DEBUG_LOG = false;

    private final Context mContext;
    private final OkHttpClient mClient;
    private final Calendar mStartDate;

    private final int[] mBloodCenterId;// = {0, 2, 3, 4, 5, 6, 7};
    private final String[] mBloodType;// = {"A", "B", "O", "AB"};
    private final String[] mBaseCityUrls;
    private final String[] mBaseCityIds;
    private SparseArray<String> mCityName;

    public BloodDataHelper(Context context) {
        mContext = context;
        mClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)//connect timeout
                .readTimeout(10, TimeUnit.SECONDS)//socket timeout
                .build();
        mStartDate = Calendar.getInstance(Locale.TAIWAN);

        Resources res = context.getResources();
        mBloodCenterId = res.getIntArray(R.array.blood_center_id);
        mBloodType = res.getStringArray(R.array.blood_type_value);
        mBaseCityUrls = res.getStringArray(R.array.blood_center_donate_station);
        mBaseCityIds = res.getStringArray(R.array.blood_center_donate_station_city_id);

        mCityName = new SparseArray<>();
    }

    public void warmup() {
        Request request = new Request.Builder()
                .url(BloodCenter.URL_BASE_BLOOD_ORG + "/robots.txt")
                .get()
                .build();
        try {
            mClient.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "warm: " + e.getMessage());
        }
    }

    private String getBody(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            // Log.d(TAG, String.format("read timeout=%d", mClient.getReadTimeout()));
            response = mClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, String.format("%s from %s", e.getMessage(), url));
        } catch (NullPointerException e) {
            Log.e(TAG, "null pointer exception");
        }
        return "(empty)";
    }

    /**
     * Get donation activities within this week
     *
     * @param siteID site id
     * @return donation activity list
     */
    public ArrayList<DonateDay> getLatestWeekCalendar(int siteID) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }
        ArrayList<DonateDay> week1 = getWeekCalendar(cal, String.valueOf(siteID));
        cal.add(Calendar.DAY_OF_WEEK, 7);
        ArrayList<DonateDay> week2 = getWeekCalendar(cal, String.valueOf(siteID));
        ArrayList<DonateDay> days = new ArrayList<>();
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

    /**
     * Get donation activities by days in a week
     *
     * @param calendar start date
     * @param site_id  site id
     * @return donation activity list
     */
    private ArrayList<DonateDay> getWeekCalendar(Calendar calendar, String site_id) {
        mStartDate.setTimeInMillis(calendar.getTimeInMillis());
        // Log.d(TAG, mStartDate.getTime().toString());
        ArrayList<DonateDay> donateDays = new ArrayList<>();

        String day = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(mStartDate.getTime());
        String url = BloodCenter.URL_LOCAL_BLOOD_CENTER_WEEK
                .replace("{site}", site_id)
                .replace("{date}", day);
        // Log.d(TAG, url);
        // Log.v(TAG, String.format("started with site=%s, day=%s", site_id, day));

        long startTime = System.currentTimeMillis();

        String html = getBody(url);
        // Log.v(TAG, String.format("get action wasted %d ms",
        //        ((System.currentTimeMillis() - startTime))));

        //Log.d(TAG, String.format("html length = %d", html.length()));
        if (html.contains("<div id=\"calendar\">")) {
            html = html.substring(html.indexOf("<div id=\"calendar\">"),
                    html.lastIndexOf("<div class=\"BackToTop\" id=\"toTop\">"));
            // Log.d(TAG, String.format("html size = %d", html.length()));
            String[] day_html = html.split("data-role=\"list-divider\"");
            // Log.d(TAG, String.format("total = %d days", day_html.length - 1));

            String separator = mContext.getString(R.string.pattern_separator);
            Calendar cal = mStartDate;
            for (int i = 1; i < day_html.length; i++, cal.add(Calendar.DAY_OF_MONTH, 1)) {
                ArrayList<DonateActivity> list = new ArrayList<>();
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

                    // Log.d(TAG, String.format("    name: %s", name));
                    // Log.d(TAG, String.format(" time: %s", time));
                    // Log.d(TAG, String.format("location: %s", location));
                    DonateActivity donateActivity = new DonateActivity(name, location);
                    donateActivity.setDuration(cal, time);
                    if (!list.contains(donateActivity)) {//[52]++
                        //    Log.w(TAG, String.format("already has %s, ignore it", name));
                        // } else {
                        list.add(donateActivity);
                    }
                    //Log.d(TAG, list.get(list.size() - 1).toString());
                }
                //Log.d(TAG, String.format("===> %s: %d", cal.getTime().toString(), list.size()));
                donateDays.add(new DonateDay(list));
                donateDays.get(donateDays.size() - 1).setDate(cal);
            }
        }

        long endTime = System.currentTimeMillis();
        if (DEBUG_LOG) {
            Log.v(TAG, String.format("end with site=%s, day=%s, wasted %d ms",
                    site_id, day, ((endTime - startTime))));
        }
        return donateDays;
    }

    /* <div class='StorageCondition'><img src='images/StorageIcon003.jpg' */
    private final static String PATTERN_STORAGE = "StorageIcon([^.]+).jpg";
    private SparseArray<HashMap<String, Integer>> mBloodStorage;

    public SparseArray<HashMap<String, Integer>> getBloodStorage() {
        return getBloodStorage(true);
    }

    /**
     * Get blood center storage
     *
     * @param forceRefresh true if to refresh data from server
     * @return storage list by blood center
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
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

        String html = getBody(BloodCenter.URL_BLOOD_STORAGE);
        // Log.v(TAG, String.format("get storage wasted %d ms",
        //        ((System.currentTimeMillis() - startTime))));

        // StorageBoard.jpg
        if (html.contains("tool_blood_cube") && html.contains("tool_danger")) {
            html = html.substring(html.indexOf("tool_blood_cube"), html.indexOf("tool_danger"));
            String[] storages = html.split("StorageHeader");
            for (int i = 1; i < storages.length; i++) {
                // Log.d(TAG, String.format("site=%d", mBloodCenterId[i]));
                int j = 0;
                HashMap<String, Integer> storageMap = new HashMap<>();
                Matcher matcher = Pattern.compile(PATTERN_STORAGE).matcher(storages[i]);
                while (matcher.find() && j < 4) {
                    // Log.d(TAG, String.format("  id=%d", Integer.parseInt(matcher.group(1))));
                    int type;
                    try {
                        type = Integer.parseInt(matcher.group(1));
                    } catch (Exception e) {
                        type = 0;
                    }
                    storageMap.put(mBloodType[j++], type);
                }
                mBloodStorage.put(mBloodCenterId[i], storageMap);
            }
        } else {
            Log.w(TAG, "no storage data?");
        }

        if (DEBUG_LOG) {
            Log.v(TAG, String.format("end storage wasted %d ms",
                    ((System.currentTimeMillis() - startTime))));
        }
        return mBloodStorage;
    }

    /**
     * Get blood center name by ID
     *
     * @param siteID site id
     * @return blood center name
     */
    public String getBloodCenterName(int siteID) {
        if (mContext == null) {
            return "";
        }
        int i;
        for (i = mBloodCenterId.length - 1; i > 0; i--) {
            if (mBloodCenterId[i] == siteID) {
                break;
            }
        }
        return mContext.getResources().getStringArray(R.array.blood_center)[i];
    }

    /**
     * Get blood center name from resources.
     *
     * @param context Context
     * @param siteId  blood center site id
     * @return blood center name
     */
    public static String getBloodCenterName(Context context, int siteId) {
        if (context == null) {
            return "";
        }
        int[] bloodCenterIds = context.getResources().getIntArray(R.array.blood_center_id);
        int i;
        for (i = bloodCenterIds.length - 1; i > 0; i--) {
            if (bloodCenterIds[i] == siteId) {
                break;
            }
        }
        return context.getResources().getStringArray(R.array.blood_center)[i];
    }

    //<option selected="selected" value="13">臺北市</option>
    private final static String PATTERN_CITY_ID = "<option([ ]selected=[^ ]+)?" +
            " value=\"([\\d]+)[^>]>([^<]+)";

    //<td width='90%'><a class='font002' href='LocationMap.aspx?spotID=79&cityID=13'
    // data-ajax='false'> 公園號捐血車</a></td>
    private final static String PATTERN_SPOT_INFO = "LocationMap.aspx\\?spotID=([\\d]+)" +
            "&cityID=([\\d]+)[^>]*>([^<]*)</a>";

    /**
     * Query blood center cover city donation spots
     *
     * @param siteId blood center site id
     * @return donation spot list
     */
    public SparseArray<SpotList> getDonationSpotLocationMap(int siteId) {
        if (mContext == null) {
            return null;
        }
        int i;
        for (i = mBloodCenterId.length - 1; i > 0; i--) {
            if (mBloodCenterId[i] == siteId) {
                break;
            }
        }

        SparseArray<SpotList> maps = new SparseArray<>();
        mCityOrder.clear();
        long startTime = System.currentTimeMillis();
        for (String cityId : mBaseCityIds[i].split(",")) {
            mCityOrder.add(cityId);
            String url = mBaseCityUrls[i].concat(BloodCenter.QS_LOCATION_MAP_CITY)
                    .replace("{city}", cityId);
            //Log.d(TAG, url);
            long s1 = System.currentTimeMillis();
            String html = getBody(url);
            if (DEBUG_LOG) {
                Log.d(TAG, "cost " + (System.currentTimeMillis() - s1));
            }
            if (html.contains("CalendarContentRight")) {
                html = html.substring(html.indexOf("CalendarContentRight"),
                        html.indexOf("ShortCutBox"));
                if (html.contains("SelectCity")) {//cache the city name
                    Matcher matcher = Pattern.compile(PATTERN_CITY_ID).matcher(html);
                    while (matcher.find()) {
                        String id = matcher.group(2).trim();
                        String name = matcher.group(3).trim();
                        // Log.d(TAG, "id=" + id + ", " + name);
                        mCityName.put(Integer.parseInt(id), name);
                    }
                }
                //Log.d(TAG, String.format("html: %d", html.length()));
                if (html.contains("InnerLocation001")) {
                    SpotList list = new SpotList(cityId);
                    String[] locations = html.split("InnerLocation001");
                    if (locations.length > 0) {//static locations
                        //Log.d(TAG, "static locations:");
                        parseDonationSpotHtml(siteId, list, locations[1], false);
                    }
                    if (locations.length > 1) {//dynamic locations
                        //Log.d(TAG, "dynamic locations:");
                        parseDonationSpotHtml(siteId, list, locations[2], false);
                    }
                    list.setCityName(mCityName.get(Integer.parseInt(cityId)));
                    maps.put(Integer.parseInt(cityId), list);
                }
            }
        }
        if (DEBUG_LOG) {
            Log.v(TAG, String.format("end spot map wasted %d ms",
                    ((System.currentTimeMillis() - startTime))));
        }
        return maps;
    }

    /**
     * Get city name by od
     *
     * @param cityId city id
     * @return city name
     */
    public String getCityName(int cityId) {
        return mCityName.get(cityId);
    }

    /**
     * Get city name list of current query. This is useful when showing city donation spot.
     *
     * @return city name list
     */
    public SparseArray<String> getCityList() {
        return mCityName;
    }

    /**
     * Set city name list for future query when using {@link #getCityName(int)}
     *
     * @param list city name list
     */
    public void setCityList(SparseArray<String> list) {
        if (list == null) {//clear all
            mCityName.clear();
        } else {
            mCityName = list;
        }
    }

    private final ArrayList<String> mCityOrder = new ArrayList<>();

    public ArrayList<String> getCityOrder() {
        return mCityOrder;
    }

    private void parseDonationSpotHtml(int siteId, SpotList list, String html,
                                       @SuppressWarnings("SameParameterValue") boolean isStatic) {
        Matcher matcher = Pattern.compile(PATTERN_SPOT_INFO).matcher(html);
        while (matcher.find()) {
            String spotId = matcher.group(1).trim();
            String cityId = matcher.group(2).trim();
            String name = matcher.group(3).trim();
            //Log.d(TAG, "  spotId = " + spotId + ", " + name);
            try {
                SpotInfo info = new SpotInfo(spotId, cityId, name);
                info.setSiteId(siteId);
                if (isStatic) {
                    list.addStaticLocation(info);
                } else {
                    list.addDynamicLocation(info);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "NumberFormatException: " + matcher.group());
            }
        }
//        if (isStatic && list.getStaticLocations() != null) {
//            Log.d(TAG, String.format("size = %d", list.getStaticLocations().size()));
//        } else if (!isStatic && list.getDynamicLocations() != null) {
//            Log.d(TAG, String.format("size = %d", list.getDynamicLocations().size()));
//        }
    }
}
