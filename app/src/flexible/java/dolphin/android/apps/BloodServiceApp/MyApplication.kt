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

    fun setGooglePlayServiceNotSupported() {
        mHasGooglePlayService = false
    }
}
