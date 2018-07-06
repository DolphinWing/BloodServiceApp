package dolphin.android.apps.BloodServiceApp


import android.app.Application
import android.content.Context
import android.util.SparseArray
import android.util.SparseIntArray
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import dolphin.android.apps.BloodServiceApp.provider.LocaleUtil
import dolphin.android.apps.BloodServiceApp.ui.DataViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by dolphin on 2014/10/21.
 * http://wangshifuola.blogspot.tw/2011/12/androidapplicationglobal-variable.html
 */
class MyApplication : Application() {

    private var mTracker: Tracker? = null

    //don't support Google Play Services
    // When dry run is set, hits will not be dispatched, but will still be logged as
    // though they were dispatched.
    val tracker: Tracker?
        @Synchronized get() {
            if (isGooglePlayServiceNotSupported) {
                return null
            }
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(this) ?: return null
                analytics.setDryRun(resources.getBoolean(R.bool.eng_mode))

                mTracker = analytics.newTracker(getString(R.string.ga_trackingId))
                mTracker!!.enableAdvertisingIdCollection(true)
                mTracker!!.enableAutoActivityTracking(true)
                mTracker!!.enableExceptionReporting(true)
            }
            return mTracker
        }

//    private val mDonationList = SparseArray<MyDonationList>()
//    private var mBloodStorage: SparseArray<HashMap<String, Int>>? = null
//    private var mBloodStorageTimestamp: Long = 0
//    private val mSpotList = SparseArray<MySpotList>()

//    var cacheBloodStorage: SparseArray<HashMap<String, Int>>?
//        get() = if (System.currentTimeMillis() - mBloodStorageTimestamp < 3600000) {
//            mBloodStorage
//        } else null
//        set(array) {
//            mBloodStorage = array
//            mBloodStorageTimestamp = System.currentTimeMillis()
//        }

    private var mHasGooglePlayService = true

    private val isGooglePlayServiceNotSupported: Boolean
        get() = !mHasGooglePlayService

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.onAttach(base))
    }

    internal val executor: ExecutorService = Executors.newFixedThreadPool(3)
    internal var storageCache: DataViewModel.StorageData? = null
    internal var donationCache = SparseArray<DataViewModel.DonationData>()
    internal val spotCityCache = SparseArray<DataViewModel.SpotData>()

//    //make a application level cache for quick switch, no need to pull from server in a short time
//    private class MyDonationList {
//        internal var days: ArrayList<DonateDay>? = null
//        internal var timestamp: Long = 0
//    }
//
//    private class MySpotList {
//        internal var spots: SparseArray<SpotList>? = null
//        internal var cities: SparseArray<String>? = null
//        internal var timestamp: Long = 0
//    }
//
//    fun setCacheDonationList(siteId: Int, days: ArrayList<DonateDay>) {
//        var list: MyDonationList? = mDonationList.get(siteId)
//        if (list == null) {
//            list = MyDonationList()
//        }
//        list.days = days
//        list.timestamp = System.currentTimeMillis()
//        mDonationList.put(siteId, list)
//    }
//
//    fun getCacheDonationList(siteId: Int): ArrayList<DonateDay>? {
//        val list = mDonationList.get(siteId)
//        if (list != null) {
//            if (System.currentTimeMillis() - list.timestamp < 3600000) {
//                return list.days
//            }
//        }
//        return null
//    }
//
//    fun setCacheSpotList(siteId: Int, spots: SparseArray<SpotList>, cities: SparseArray<String>) {
//        var list: MySpotList? = mSpotList.get(siteId)
//        if (list == null) {
//            list = MySpotList()
//        }
//        list.spots = spots
//        list.timestamp = System.currentTimeMillis()
//        list.cities = cities
//        mSpotList.put(siteId, list)
//    }
//
//    fun getCacheSpotList(siteId: Int): SparseArray<SpotList>? {
//        val list = mSpotList.get(siteId)
//        if (list != null) {
//            if (System.currentTimeMillis() - list.timestamp < 3600000) {
//                return list.spots
//            }
//        }
//        return null
//    }
//
//    fun getCacheCityList(siteId: Int): SparseArray<String>? {
//        val list = mSpotList.get(siteId)
//        if (list != null) {
//            if (System.currentTimeMillis() - list.timestamp < 3600000) {
//                return list.cities
//            }
//        }
//        return null
//    }

    fun setGooglePlayServiceNotSupported() {
        mHasGooglePlayService = false
    }
}
