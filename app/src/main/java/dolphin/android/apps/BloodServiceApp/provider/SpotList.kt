package dolphin.android.apps.BloodServiceApp.provider

import androidx.annotation.Keep
import java.util.*

/**
 * Created by jimmyhu on 2017/2/22.
 * Spot list of a city/region.
 */
@Keep
class SpotList(
        /**
         * get city id of this region

         * @return city id
         */
        val cityId: Int) {
    /**
     * get full static spot location list

     * @return static spot location list
     */
    var staticLocations: ArrayList<SpotInfo> = ArrayList()
        private set
    /**
     * get full dynamic spot location list

     * @return dynamic spot location list
     */
    var dynamicLocations: ArrayList<SpotInfo> = ArrayList()
        private set

    constructor(cityId: String) : this(Integer.parseInt(cityId))

    /**
     * add a new spot location to static location list

     * @param spotInfo a new spot location
     */
    fun addStaticLocation(spotInfo: SpotInfo) {
        staticLocations.add(spotInfo)
    }

    /**
     * add a new spot location to dynamic location list. the spot may be moved from time to time.

     * @param spotInfo a new spot location
     */
    fun addDynamicLocation(spotInfo: SpotInfo) {
        dynamicLocations.add(spotInfo)
    }

    /**
     * get full spot location list including static and dynamic locations

     * @return spot location list
     */
    val locations: ArrayList<SpotInfo>
        get() {
            val list = ArrayList<SpotInfo>()
            list.addAll(staticLocations)
            list.addAll(dynamicLocations)
            return list
        }
}
