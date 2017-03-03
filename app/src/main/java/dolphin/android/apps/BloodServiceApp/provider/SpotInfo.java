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

    /**
     * get donation spot id
     *
     * @return donation spot id
     */
    public int getSpotId() {
        return spot_id;
    }

    /**
     * get donation spot city id
     *
     * @return city id
     */
    public int getCityId() {
        return city_id;
    }

    /**
     * get donation spot name
     *
     * @return donation spot name
     */
    public String getSpotName() {
        return spot_name;
    }

    /**
     * get blood center site id of donation spot
     *
     * @return blood center site id
     */
    public int getSiteId() {
        return site_id;
    }

    /**
     * set blood center site id of donation spot
     *
     * @param siteId blood center site id
     */
    public void setSiteId(int siteId) {
        site_id = siteId;
    }
}
