package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.util.readFromAssets
import dolphin.android.util.readFromRaw
import java.util.Calendar

class BloodDataReaderTestImpl(private val context: Context) : BloodDataReader {
    /**
     * set which resource data to be used in the following test
     */
    var contentAsset: String? = null

    override fun warmUp() {
        // do nothing
    }

    override fun readBloodStorage(): String {
        return contentAsset?.let { readFromAssets(it) } ?: readFromRaw(R.raw.blood_center_storage)
        // return readFromAssets(contentAsset ?: "blood_center_storage.txt")
    }

    override fun readWeekCalendar(id: Int, weekDay: Calendar): String {
        return readFromAssets(contentAsset ?: "local_blood_center_${id}_week.txt")
    }

    override fun readDonationSpotList(centerId: Int, cityId: Int, url: String): String {
        return readFromAssets(contentAsset ?: "location_map_${centerId}_$cityId.txt")
    }

    private fun readFromAssets(asset: String): String = context.readFromAssets(asset) ?: ""

    private fun readFromRaw(id: Int): String = context.readFromRaw(id) ?: ""
}
