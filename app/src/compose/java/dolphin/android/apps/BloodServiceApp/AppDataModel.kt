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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

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
     * @return true if update in progressing
     */
    fun getDonationData(
        helper: BloodDataHelper,
        id: Int = center.value?.id ?: -1
    ): StateFlow<Boolean> = flow {
        if (donationCache[id] == null) {
            _daysList.emit(ArrayList()) // clear the list
            donationCache.put(id, helper.getLatestWeekCalendar(id))
        }
        _daysList.emit(donationCache[id])
        emit(false)
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    private val _daysList = MutableStateFlow<List<DonateDay>>(ArrayList())

    /**
     * A donation list live data to update compose ui.
     */
    val daysList: StateFlow<List<DonateDay>> = _daysList

    private val spotCityCache = SparseArray<ArrayList<SpotList>>()

    /**
     * Get donation spot list from internet.
     *
     * @param helper a helper instance to read data from internet
     * @param id target blood center id
     * @return true if update in progressing
     */
    fun getSpotListData(
        helper: BloodDataHelper,
        id: Int = center.value?.id ?: -1
    ): StateFlow<Boolean> = flow {
        if (spotCityCache[id] == null) {
            val list = ArrayList<SpotList>()
            updateSpotList(list) // clear the list

            val data = helper.getDonationSpotLocationMap(id)
            helper.cityOrder?.forEach { id ->
                val cityId = id.toInt()
                list.add(data.get(cityId).apply { cityName = helper.getCityName(cityId) })
            }
            spotCityCache.put(id, list)
        }
        updateSpotList(list = spotCityCache[id])
        emit(false)
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    private val _spotList = MutableStateFlow<List<SpotList>>(ArrayList())

    /**
     * A spot list live data to update compose ui.
     */
    val spotList: StateFlow<List<SpotList>> = _spotList

    /**
     * Update spot list to compose ui.
     *
     * @param list spot list of target blood center
     */
    private suspend fun updateSpotList(list: List<SpotList>) {
        _spotList.emit(list)
        currentCity.emit(if (list.isNotEmpty()) list.first().cityId else 0)
    }

    private val currentCity = MutableStateFlow(0)

    /**
     * Current selected city. Only useful in SpotListUi.
     */
    val city: StateFlow<Int> = currentCity

    /**
     * Change a new city
     *
     * @param cityId new city
     */
    fun changeCity(cityId: Int) {
        currentCity.value = cityId
    }
}