package dolphin.android.apps.BloodServiceApp.provider

import androidx.annotation.Keep

/**
 * Donation Spot information
 */
@Keep
data class SpotInfo(
    /**
     * donation spot id
     */
    val spotId: Int,

    /**
     * donation spot city id
     */
    val cityId: Int,

    /**
     * donation spot name
     */
    val spotName: String,

    /**
     * blood center site id of donation spot
     */
    var siteId: Int = 0
) {
    constructor(spotId: String, cityId: String, name: String) :
        this(
            try {
                Integer.parseInt(spotId)
            } catch (e: NumberFormatException) {
                0
            },
            try {
                Integer.parseInt(cityId)
            } catch (e: NumberFormatException) {
                0
            },
            name,
        )
}
