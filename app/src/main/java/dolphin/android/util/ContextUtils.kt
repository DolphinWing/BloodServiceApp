package dolphin.android.util

import android.content.Context
import java.io.IOException
import java.io.InputStreamReader

/**
 * Read content from assets
 *
 * @param asset asset name
 * @param encoding charset name
 * @return string content
 */
fun Context.readFromAssets(asset: String, encoding: String = "UTF8"): String? {
    try {
        val sr = InputStreamReader(assets.open(asset), encoding)
        var len: Int
        val sb = StringBuilder()
        while (true) {
            val buffer = CharArray(1024)
            len = sr.read(buffer)
            if (len > 0) {
                sb.append(buffer)
            } else {
                break
            }
        }
        sr.close()
        return sb.toString().trim { it <= ' ' }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

/**
 * Read content from res/raw
 *
 * @param id resource id
 * @return string content
 */
fun Context.readFromRaw(id: Int): String? {
    val inputStream = resources.openRawResource(id)
    val size = try {
        inputStream.available()
    } catch (e: IOException) {
        -1
    }
    return if (size > 0) {
        val sr = InputStreamReader(inputStream)
        var len: Int
        val sb = StringBuilder()
        while (true) {
            val buffer = CharArray(1024)
            len = sr.read(buffer)
            if (len > 0) {
                sb.append(buffer)
            } else {
                break
            }
        }
        sr.close()
        sb.toString()
    } else {
        null
    }
}
