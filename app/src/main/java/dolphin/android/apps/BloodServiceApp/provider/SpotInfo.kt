package dolphin.android.apps.BloodServiceApp.provider

import androidx.annotation.Keep

/**
 * Created by jimmyhu on 2017/2/22.
 * Donation Spot information
 */
@Keep
data class SpotInfo(
        /**
         * get donation spot id

         * @return donation spot id
         */
        val spotId: Int,
        /**
         * get donation spot city id

         * @return city id
         */
        val cityId: Int,
        /**
         * get donation spot name

         * @return donation spot name
         */
        val spotName: String) {
    /**
     * get blood center site id of donation spot

     * @return blood center site id
     */
    /**
     * set blood center site id of donation spot

     * @param siteId blood center site id
     */
    var siteId: Int = 0

    constructor(spotId: String, cityId: String, name: String) : this(Integer.parseInt(spotId),
            Integer.parseInt(cityId), name)
}
