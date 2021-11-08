package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import java.io.IOException
import java.io.InputStreamReader
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
        return readFromAssets(contentAsset ?: "location_map_${centerId}_${cityId}.txt")
    }

    private fun readFromAssets(asset: String): String =
        PrefsUtil.read_asset_text(context, asset, null) ?: ""

    private fun readFromRaw(id: Int): String {
        val inputStream = context.resources.openRawResource(id)
        val size = try {
            inputStream.available()
        } catch (e: IOException) {
            -1
        }
        if (size > 0) {
            val sr = InputStreamReader(inputStream)
            // Log.i(TAG, asset_name + " " + sr.getEncoding());
            var len: Int
            val sb = StringBuilder()
            while (true) { // read from buffer
                val buffer = CharArray(1024)
                len = sr.read(buffer) // , size, 512);
                // Log.d(TAG, String.format("%d", len));
                if (len > 0) {
                    sb.append(buffer)
                } else {
                    break
                }
            }
            sr.close()
            return sb.toString()
        }
        return ""
    }
}