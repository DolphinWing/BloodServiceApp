package dolphin.android.apps.BloodServiceApp.provider;

/**
 * Created by jimmyhu on 2017/2/22.
 * Donation Spot information
 */

public class SpotInfo {
    private int site_id;
    private int spot_id;
    private int city_id;
    private String spot_name;

    public SpotInfo(String spotId, String cityId, String name) {
        this(Integer.parseInt(spotId), Integer.parseInt(cityId), name);
    }

    public SpotInfo(int spotId, int cityId, String name) {
        spot_id = spotId;
        city_id = cityId;
        spot_name = name;
    }

    public int getSpotId() {
        return spot_id;
    }

    public int getCityId() {
        return city_id;
    }

    public String getSpotName() {
        return spot_name;
    }

    public int getSiteId() {
        return site_id;
    }

    public void setSiteId(int siteId) {
        site_id = siteId;
    }
}
