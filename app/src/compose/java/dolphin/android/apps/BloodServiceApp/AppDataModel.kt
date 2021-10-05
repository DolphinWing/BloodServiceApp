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

/**
 * App data [ViewModel] in [SavedStateHandle].
 *
 * @param savedState saved app state
 */
class AppDataModel(private val savedState: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_UI_STATE = "ui_state"
        private const val KEY_LOADING = "loading"
    }

    /**
     * A flag of app about Firebase RemoteConfig load up.
     */
    val ready = MutableLiveData(false)

    /**
     * App UI state.
     */
    val uiState: LiveData<UiState> = savedState.getLiveData(KEY_UI_STATE)

    /**
     * Internal app ui state
     */
    var state: UiState
        get() = uiState.value ?: UiState.Main
        private set(value) {
            savedState.set(KEY_UI_STATE, value)
        }

    /**
     * Change app ui state
     *
     * @param state target app ui state
     */
    fun changeUiState(state: UiState) {
        this.state = state
    }

    // val loading: LiveData<Boolean> = savedState.getLiveData(KEY_LOADING)

    /**
     * Set app loading state.
     *
     * @param value true if app is loading something in the background
     */
    fun loading(value: Boolean) {
        savedState.set(KEY_LOADING, value)
    }

    /**
     * Selected blood center
     */
    val center = MutableLiveData<BloodCenter.Center>()

    /**
     * Initialize the helper and other data instance.
     *
     * @param helper a helper instance to read data from internet
     * @return true if helper is ready
     */
    fun init(helper: BloodDataHelper): LiveData<Boolean> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            helper.warmup()
            // getStorageData(helper, true)
            emit(true)
        }

    private var storageCache: SparseArray<HashMap<String, Int>>? = null

    /**
     * Get storage data from internet
     *
     * @param helper a helper instance to read data from internet
     * @param forceRefresh true if we want to download latest data from internet
     * @return all storage data
     */
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

    /**
     * Get blood center storage data
     *
     * @param id target blood center id
     * @return storage data of target blood center
     */
    fun getStorageData(id: Int = center.value?.id ?: -1): HashMap<String, Int> {
        return storageCache?.get(id) ?: HashMap()
    }

    private val storages = MutableLiveData<HashMap<String, Int>>()

    /**
     * A storage cache live data to update compose ui.
     */
    val storageMap: LiveData<HashMap<String, Int>> = storages

    /**
     * Update storage data to compose ui.
     *
     * @param id target blood center id
     * @param maps target blood center storage
     */
    fun updateStorageMap(id: Int = center.value?.id ?: -1, maps: HashMap<String, Int>) {
        storages.postValue(maps)
    }

    private var donationCache = SparseArray<ArrayList<DonateDay>>()

    /**
     * Get donation list from internet.
     *
     * @param helper a helper instance to read data from internet
     * @param id target blood center id
     */
    fun getDonationData(
        helper: BloodDataHelper,
        id: Int = center.value?.id ?: -1
    ): LiveData<ArrayList<DonateDay>> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (donationCache[id] == null) {
                donationCache.put(id, helper.getLatestWeekCalendar(id))
            }
            emit(donationCache[id])
        }

    private val _daysList = MutableLiveData<List<DonateDay>>()

    /**
     * A donation list live data to update compose ui.
     */
    val daysList: LiveData<List<DonateDay>> = _daysList

    /**
     * Update donation list to compose ui.
     *
     * @param id target blood center id
     * @param list donation list of target blood center
     */
    fun updateEventList(id: Int = center.value?.id ?: -1, list: List<DonateDay>) {
        _daysList.postValue(list)
    }

    private val spotCityCache = SparseArray<ArrayList<SpotList>>()

    /**
     * Get donation list from internet.
     *
     * @param helper a helper instance to read data from internet
     * @param id target blood center id
     */
    fun getSpotList(
        helper: BloodDataHelper,
        id: Int = center.value?.id ?: -1
    ): LiveData<ArrayList<SpotList>> =
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if (spotCityCache[id] == null) {
                val data = helper.getDonationSpotLocationMap(id)
                val list = ArrayList<SpotList>()
                helper.cityOrder?.forEach { id ->
                    val cityId = id.toInt()
                    //application.cityNameCache.put(cityId, helper.getCityName(cityId))
                    list.add(data.get(cityId).apply { cityName = helper.getCityName(cityId) })
                }
                spotCityCache.put(id, list)
            }
            emit(spotCityCache[id])
        }

    private val _spotList = MutableLiveData<List<SpotList>>()

    /**
     * A spot list live data to update compose ui.
     */
    val spotList: LiveData<List<SpotList>> = _spotList

    /**
     * Update spot list to compose ui.
     *
     * @param id target blood center id
     * @param list spot list of target blood center
     */
    fun updateSpotList(id: Int = center.value?.id ?: -1, list: List<SpotList>) {
        _spotList.postValue(list)
    }
}