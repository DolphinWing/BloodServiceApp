package dolphin.android.apps.BloodServiceApp.provider

import androidx.annotation.Keep

/**
 * Donation location
 *
 * Created by jimmyhu on 2015/6/3.
 */
@Keep
data class DonateLocation(var isFixed: Boolean, var name: String?, val spotId: String,
                          val cityId: String, var address: String?, var phone: String?,
                          var operationTime: String?, var extraMessage: String?, val mapUrl: String)
