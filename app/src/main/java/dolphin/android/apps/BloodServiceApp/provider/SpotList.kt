package dolphin.android.apps.BloodServiceApp.provider

import androidx.annotation.Keep

/**
 * Spot list of a city/region.
 */
@Keep
class SpotList(
    /**
     * city id of this region
     */
    val cityId: Int,

    /**
     * city name
     */
    var cityName: String? = null,
) {
    /**
     * full static spot location list
     */
    private val staticLocations: ArrayList<SpotInfo> = ArrayList()

    /**
     * full dynamic spot location list
     */
    private val dynamicLocations: ArrayList<SpotInfo> = ArrayList()

    constructor(cityId: String) : this(cityId.toInt())

    /**
     * add a new spot location to static location list
     *
     * @param spotInfo a new spot location
     */
    fun addStaticLocation(spotInfo: SpotInfo) {
        staticLocations.add(spotInfo)
    }

    /**
     * add a new spot location to dynamic location list. the spot may be moved from time to time.
     *
     * @param spotInfo a new spot location
     */
    fun addDynamicLocation(spotInfo: SpotInfo) {
        dynamicLocations.add(spotInfo)
    }

    /**
     * full spot location list including static and dynamic locations
     */
    val locations: ArrayList<SpotInfo>
        get() = ArrayList<SpotInfo>().apply {
            addAll(staticLocations)
            addAll(dynamicLocations)
        }
}
