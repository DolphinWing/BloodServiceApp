package dolphin.android.apps.BloodServiceApp.provider;

import java.util.ArrayList;

/**
 * Created by jimmyhu on 2017/2/22.
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

    public void addStaticLocation(SpotInfo spotInfo) {
        if (static_locations == null) {
            static_locations = new ArrayList<>();
        }
        static_locations.add(spotInfo);
    }

    public void addDynamicLocation(SpotInfo spotInfo) {
        if (dynamic_locations == null) {
            dynamic_locations = new ArrayList<>();
        }
        dynamic_locations.add(spotInfo);
    }

    public int getCityId() {
        return city_id;
    }

    public ArrayList<SpotInfo> getStaticLocations() {
        return static_locations;
    }

    public ArrayList<SpotInfo> getDynamicLocations() {
        return dynamic_locations;
    }

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
