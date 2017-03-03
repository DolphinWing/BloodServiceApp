package dolphin.android.apps.BloodServiceApp.provider;

import java.util.ArrayList;

/**
 * Created by jimmyhu on 2017/2/22.
 * Spot list of a city/region.
 */

public class SpotList {
    private int city_id;
    private ArrayList<SpotInfo> static_locations;
    private ArrayList<SpotInfo> dynamic_locations;

    public SpotList(int cityId) {
        city_id = cityId;
    }

    public SpotList(String cityId) {
        this(Integer.parseInt(cityId));
    }

    /**
     * add a new spot location to static location list
     *
     * @param spotInfo a new spot location
     */
    public void addStaticLocation(SpotInfo spotInfo) {
        if (static_locations == null) {
            static_locations = new ArrayList<>();
        }
        static_locations.add(spotInfo);
    }

    /**
     * add a new spot location to dynamic location list. the spot may be moved from time to time.
     *
     * @param spotInfo a new spot location
     */
    public void addDynamicLocation(SpotInfo spotInfo) {
        if (dynamic_locations == null) {
            dynamic_locations = new ArrayList<>();
        }
        dynamic_locations.add(spotInfo);
    }

    /**
     * get city id of this region
     *
     * @return city id
     */
    public int getCityId() {
        return city_id;
    }

    /**
     * get full static spot location list
     *
     * @return static spot location list
     */
    public ArrayList<SpotInfo> getStaticLocations() {
        return static_locations;
    }

    /**
     * get full dynamic spot location list
     *
     * @return dynamic spot location list
     */
    public ArrayList<SpotInfo> getDynamicLocations() {
        return dynamic_locations;
    }

    /**
     * get full spot location list including static and dynamic locations
     *
     * @return spot location list
     */
    public ArrayList<SpotInfo> getLocations() {
        ArrayList<SpotInfo> list = new ArrayList<>();
        if (getStaticLocations() != null) {
            list.addAll(getStaticLocations());
        }
        if (getDynamicLocations() != null) {
            list.addAll(getDynamicLocations());
        }
        return list;
    }
}
