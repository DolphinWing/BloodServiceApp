package dolphin.android.apps.BloodServiceApp.provider

import androidx.annotation.Keep
import dolphin.android.util.DeprecatedNoCoverage

/**
 * Donation location
 * @suppress
 */
@DeprecatedNoCoverage
@Deprecated("no longer needed", replaceWith = ReplaceWith("SpotInfo and SpotList"))
@Keep
data class DonateLocation(
    var isFixed: Boolean,
    var name: String? = null,
    val spotId: String,
    val cityId: String,
    val mapUrl: String,
    var address: String? = null,
    var phone: String? = null,
    var operationTime: String? = null,
    var extraMessage: String? = null,
)
