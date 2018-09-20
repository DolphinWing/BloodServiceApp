package dolphin.android.apps.BloodServiceApp.ui

import android.app.Application
import android.util.Log
import android.util.SparseArray
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dolphin.android.apps.BloodServiceApp.MyApplication
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import dolphin.android.apps.BloodServiceApp.provider.SpotList
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList

/*internal*/ class DataViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "DataViewModel"
    }

    private val application: MyApplication = app as MyApplication
    private val helper = BloodDataHelper(application)

    fun getStorageData(): StorageData? {
        if (application.storageCache == null) {
            application.storageCache = StorageData(application.executor, helper)
        }
        return application.storageCache
    }

    /*internal*/ class StorageData(private val executor: ExecutorService,
                               private val helper: BloodDataHelper)
        : LiveData<SparseArray<HashMap<String, Int>>>() {
        override fun onActive() {
            super.onActive()
            executor.submit { if (value == null) fetch() }
        }

        fun fetch(forceRefresh: Boolean = false) {
            postValue(helper.getBloodStorage(forceRefresh))
        }
    }

    fun getDonationData(siteId: Int): DonationData {
        if (application.donationCache[siteId] == null) {
            application.donationCache.put(siteId, DonationData(application.executor, helper, siteId))
        }
        return application.donationCache[siteId]
    }

    /*internal*/ class DonationData(private val executor: ExecutorService,
                                private val helper: BloodDataHelper,
                                private val siteId: Int) : LiveData<ArrayList<DonateDay>>() {
        override fun onActive() {
            super.onActive()
            executor.submit { if (value == null) fetch() }
        }

        private fun fetch() {
            postValue(helper.getLatestWeekCalendar(siteId))
        }
    }

    fun getSpotData(siteId: Int): SpotData {
        if (application.spotCityCache[siteId] == null) {
            application.spotCityCache.put(siteId, SpotData(application, helper, siteId))
        }
        return application.spotCityCache[siteId]
    }

    /*internal*/ class SpotData(private val application: MyApplication,
                            private val helper: BloodDataHelper,
                            private val siteId: Int) : LiveData<ArrayList<SpotList>>() {
        override fun onActive() {
            super.onActive()
            application.executor.submit { if (value == null) fetch() }
        }

        private fun fetch() {
            val data = helper.getDonationSpotLocationMap(siteId)
            //application.cityOrderCache.put(siteId, helper.cityOrder)
            Log.d(TAG, "site id: $siteId")
            val list = ArrayList<SpotList>()
            helper.cityOrder?.forEach {
                val cityId = it.toInt()
                Log.d(TAG, "  city order: $it ${helper.getCityName(cityId)}")
                //application.cityNameCache.put(cityId, helper.getCityName(cityId))
                list.add(data.get(cityId).apply { cityName = helper.getCityName(cityId) })
            }
            postValue(list)
        }
    }
}