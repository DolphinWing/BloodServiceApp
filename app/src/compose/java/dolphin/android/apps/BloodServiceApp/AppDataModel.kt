package dolphin.android.apps.BloodServiceApp

import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.BloodDataParser
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
    }

    /**
     * A flag of app about Firebase RemoteConfig load up.
     */
    val ready = MutableLiveData(false)

    /**
     * A flag of app about system dark mode is enabled or not
     */
    val darkMode = MutableStateFlow(false)

    /**
     * A flag to show/hide privacy review pane
     */
    val showPrivacyReview = MutableStateFlow(false)

    /**
     * A flag to show/hide mobile ads
     */
    val showAds = MutableStateFlow(true)

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

    /**
     * Selected blood center
     */
    val center = MutableLiveData<BloodCenter.Center>()

    /**
     * Initialize the parser and other data instance.
     *
     * @param parser a helper instance to read data from internet
     * @return true if helper is ready
     */
    fun init(parser: BloodDataParser): StateFlow<Boolean> = flow {
        parser.warmUp()
        emit(true)
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false,
    )

    private var storageCache: SparseArray<HashMap<String, Int>>? = null

    /**
     * Get storage data from internet
     *
     * @param parser a helper instance to read data from internet
     * @param forceRefresh true if we want to download latest data from internet
     * @return true if update is in progressing
     */
    fun getStorageData(
        parser: BloodDataParser,
        forceRefresh: Boolean = false,
        centerId: Int = -1,
    ): StateFlow<Boolean> = flow {
        if (storageCache == null || forceRefresh) {
            emitStorageMap(HashMap()) // clear the list
            storageCache = parser.getBloodStorage(forceRefresh)
        }
        storageCache?.let { cache ->
            if (centerId > 0) emitStorageMap(cache[centerId] ?: HashMap())
        } ?: kotlin.run {
            emitStorageMap(HashMap())
        }
        emit(false)
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    private val storages = MutableStateFlow<HashMap<String, Int>>(HashMap())

    /**
     * A storage cache live data to update compose ui.
     */
    val storageMap: StateFlow<HashMap<String, Int>> = storages

    /**
     * Update storage data to compose ui.
     *
     * @param maps target blood center storage
     */
    private suspend fun emitStorageMap(maps: HashMap<String, Int>) {
        storages.emit(maps)
    }

    private var donationCache = SparseArray<List<DonateDay>>()

    /**
     * Get donation list from internet.
     *
     * @param parser a helper instance to read data from internet
     * @param id target blood center id
     * @return true if update is in progressing
     */
    fun getDonationData(
        parser: BloodDataParser,
        id: Int = center.value?.id ?: -1
    ): StateFlow<Boolean> = flow {
        if (donationCache[id] == null) {
            _daysList.emit(ArrayList()) // clear the list
            donationCache.put(id, parser.getLatestWeekCalendar(id))
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
     * @param parser a helper instance to read data from internet
     * @param id target blood center id
     * @return true if update is in progressing
     */
    fun getSpotListData(
        parser: BloodDataParser,
        id: Int = center.value?.id ?: -1
    ): StateFlow<Boolean> = flow {
        if (spotCityCache[id] == null) {
            val list = ArrayList<SpotList>()
            emitSpotList(list) // clear the list

            val (order, data) = parser.getDonationSpotLocationMap(id)
            order.forEach { id -> list.add(data.get(id)) }
            spotCityCache.put(id, list)
        }
        emitSpotList(list = spotCityCache[id])
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
    private suspend fun emitSpotList(list: List<SpotList>) {
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
