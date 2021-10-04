package dolphin.android.apps.BloodServiceApp

import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.BloodDataHelper
import dolphin.android.apps.BloodServiceApp.provider.DonateDay
import dolphin.android.apps.BloodServiceApp.provider.SpotList
import dolphin.android.apps.BloodServiceApp.ui.UiState
import kotlinx.coroutines.Dispatchers

class AppDataModel(private val savedState: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_UI_STATE = "ui_state"
        private const val KEY_LOADING = "loading"
    }

    val ready = MutableLiveData(false)

    val uiState: LiveData<UiState> = savedState.getLiveData(KEY_UI_STATE)

    var state: UiState
        get() = uiState.value ?: UiState.Main
        private set(value) {
            savedState.set(KEY_UI_STATE, value)
        }

    fun changeUiState(state: UiState) {
        this.state = state
    }

    val loading: LiveData<Boolean> = savedState.getLiveData(KEY_LOADING)
    fun loading(value: Boolean) {
        savedState.set(KEY_LOADING, value)
    }

    val center = MutableLiveData<BloodCenter.Center>()

    fun init(helper: BloodDataHelper): LiveData<Boolean> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            helper.warmup()
            // getStorageData(helper, true)
            emit(true)
        }

    var storageCache: SparseArray<HashMap<String, Int>>? = null
    private val storages = MutableLiveData<HashMap<String, Int>>()
    val storageMap: LiveData<HashMap<String, Int>> = storages

    fun getStorageData(
        helper: BloodDataHelper,
        forceRefresh: Boolean = false
    ): LiveData<SparseArray<HashMap<String, Int>>> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (storageCache == null) {
                storageCache = helper.getBloodStorage(forceRefresh)
            }
            storageCache?.let { cache -> emit(cache) }
        }

    fun getStorageData(siteId: Int = center.value?.id ?: -1): HashMap<String, Int> {
        return storageCache?.get(siteId) ?: HashMap()
    }

    fun updateStorageMap(id: Int = center.value?.id ?: -1, maps: HashMap<String, Int>) {
        storages.postValue(maps)
    }

    private var donationCache = SparseArray<ArrayList<DonateDay>>()
    private val _daysList = MutableLiveData<List<DonateDay>>()
    val daysList: LiveData<List<DonateDay>> = _daysList

    fun getDonationData(
        helper: BloodDataHelper,
        siteId: Int = center.value?.id ?: -1
    ): LiveData<ArrayList<DonateDay>> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (donationCache[siteId] == null) {
                donationCache.put(siteId, helper.getLatestWeekCalendar(siteId))
            }
            emit(donationCache[siteId])
        }

    fun updateEventList(id: Int = center.value?.id ?: -1, list: List<DonateDay>) {
        _daysList.postValue(list)
    }

    private val spotCityCache = SparseArray<ArrayList<SpotList>>()
    private val _spotList = MutableLiveData<List<SpotList>>()
    val spotList: LiveData<List<SpotList>> = _spotList

    fun getSpotList(
        helper: BloodDataHelper,
        siteId: Int = center.value?.id ?: -1
    ): LiveData<ArrayList<SpotList>> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (spotCityCache[siteId] == null) {
                val data = helper.getDonationSpotLocationMap(siteId)
                val list = ArrayList<SpotList>()
                helper.cityOrder?.forEach { id ->
                    val cityId = id.toInt()
                    //application.cityNameCache.put(cityId, helper.getCityName(cityId))
                    list.add(data.get(cityId).apply { cityName = helper.getCityName(cityId) })
                }
                spotCityCache.put(siteId, list)
            }
            emit(spotCityCache[siteId])
        }

    fun updateSpotList(id: Int = center.value?.id ?: -1, list: List<SpotList>) {
        _spotList.postValue(list)
    }
}