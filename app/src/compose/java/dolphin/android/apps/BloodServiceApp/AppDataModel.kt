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
        forceRefresh: Boolean = false,
        centerId: Int? = -1,
    ): LiveData<Boolean> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        if (storageCache == null || forceRefresh) {
            storageCache = helper.getBloodStorage(forceRefresh)
        }
        storageCache?.let { cache ->
            centerId?.let { id -> updateStorageMap(cache[id]) }
        } ?: kotlin.run {
            updateStorageMap(HashMap())
        }
        emit(true)
    }

    private val storages = MutableLiveData<HashMap<String, Int>>()

    /**
     * A storage cache live data to update compose ui.
     */
    val storageMap: LiveData<HashMap<String, Int>> = storages

    /**
     * Update storage data to compose ui.
     *
     * @param maps target blood center storage
     */
    private fun updateStorageMap(maps: HashMap<String, Int>) {
        storages.postValue(maps)
    }

    private var donationCache = SparseArray<ArrayList<DonateDay>>()

    /**
     * Get donation list from internet.
     *
     * @param helper a helper instance to read data from internet
     * @param id target blood center id
     * @return true if update success
     */
    fun getDonationData(
        helper: BloodDataHelper,
        id: Int = center.value?.id ?: -1
    ): LiveData<Boolean> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        if (donationCache[id] == null) {
            donationCache.put(id, helper.getLatestWeekCalendar(id))
        }
        updateEventList(donationCache[id])
        emit(true)
    }

    private val _daysList = MutableLiveData<List<DonateDay>>()

    /**
     * A donation list live data to update compose ui.
     */
    val daysList: LiveData<List<DonateDay>> = _daysList

    /**
     * Update donation list to compose ui.
     *
     * @param list donation list of target blood center
     */
    private fun updateEventList(list: List<DonateDay>) {
        _daysList.postValue(list)
    }

    private val spotCityCache = SparseArray<ArrayList<SpotList>>()

    /**
     * Get donation list from internet.
     *
     * @param helper a helper instance to read data from internet
     * @param id target blood center id
     * @return true if update success
     */
    fun getSpotList(
        helper: BloodDataHelper,
        id: Int = center.value?.id ?: -1
    ): LiveData<Boolean> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(false)
        if (spotCityCache[id] == null) {
            updateSpotList(list = ArrayList()) // empty the list first
            val data = helper.getDonationSpotLocationMap(id)
            val list = ArrayList<SpotList>()
            helper.cityOrder?.forEach { id ->
                val cityId = id.toInt()
                list.add(data.get(cityId).apply { cityName = helper.getCityName(cityId) })
            }
            spotCityCache.put(id, list)
        }
        updateSpotList(list = spotCityCache[id])
        emit(true)
    }

    private val _spotList = MutableLiveData<List<SpotList>>()

    /**
     * A spot list live data to update compose ui.
     */
    val spotList: LiveData<List<SpotList>> = _spotList

    /**
     * Update spot list to compose ui.
     *
     * @param list spot list of target blood center
     */
    private fun updateSpotList(list: List<SpotList>) {
        _spotList.postValue(list)
        changeCity(if (list.isNotEmpty()) list.first().cityId else 0)
    }

    /**
     * Current selected city. Only useful in SpotListUi.
     */
    val currentCity = MutableLiveData<Int>()

    /**
     * Change a new city
     *
     * @param cityId new city
     */
    fun changeCity(cityId: Int) {
        currentCity.postValue(cityId)
    }
}