package dolphin.android.apps.BloodServiceApp

import android.content.Context
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.BloodDataParser
import dolphin.android.apps.BloodServiceApp.provider.BloodDataReaderTestImpl
import dolphin.android.apps.BloodServiceApp.ui.UiState
import dolphin.android.tests.CoroutineTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.R])
@RunWith(AndroidJUnit4::class)
class AppDataModelTest : CoroutineTest() {
    private lateinit var model: AppDataModel
    private lateinit var parser: BloodDataParser
    private lateinit var center: BloodCenter

    @Before
    fun setup() {
        model = AppDataModel(SavedStateHandle())

        val context = ApplicationProvider.getApplicationContext<Context>()
        val reader = BloodDataReaderTestImpl(context)
        parser = BloodDataParser(context, reader)
        center = BloodCenter(context)
    }

    @Test
    fun ready() = runCoroutineTest {
        model.ready.observeForever { actual ->
            Assert.assertFalse(actual)
        }
    }

    @Test
    fun postReady() = runCoroutineTest {
        model.ready.postValue(true)
        model.ready.observeForever { actual ->
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun darkMode() = runCoroutineTest {
        model.darkMode.asLiveData().observeForever { actual ->
            Assert.assertFalse(actual)
        }
    }

    @Test
    fun changeDarkMode() = runCoroutineTest {
        model.darkMode.emit(true)
        model.darkMode.asLiveData().observeForever { actual ->
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun noPrivacyUpdate() = runCoroutineTest {
        model.showPrivacyReview.asLiveData().observeForever { actual ->
            Assert.assertFalse(actual)
        }
    }

    @Test
    fun reviewPrivacy() = runCoroutineTest {
        model.showPrivacyReview.emit(true)
        model.showPrivacyReview.asLiveData().observeForever { actual ->
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun uiState() {
        Assert.assertEquals(UiState.Main, model.state)
    }

    @Test
    fun changeUiState() = runCoroutineTest {
        model.changeUiState(UiState.Welcome)
        model.uiState.observeForever { actual ->
            Assert.assertEquals(UiState.Welcome, actual)
            Assert.assertEquals(actual, model.state)
        }
    }

    @Test
    fun warmUp() = runCoroutineTest {
        model.init(parser).asLiveData().observeForever { actual ->
            Assert.assertTrue(actual) // always true
        }
    }

    @Test
    fun getStorageData() = runCoroutineTest {
        model.getStorageData(parser).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getStorageDataRefresh() = runCoroutineTest {
        model.getStorageData(parser, true).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getStorageDataCache() = runCoroutineTest {
        repeat(2) {
            model.getStorageData(parser).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun getStorageDataCacheRefresh() = runCoroutineTest {
        repeat(2) {
            model.getStorageData(parser, true).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun storageMap() = runCoroutineTest {
        model.getStorageData(parser, centerId = center.taipei().id)
        model.storageMap.asLiveData().observeForever { map ->
            Assert.assertTrue(map.isNotEmpty())
        }
    }

    @Test
    fun getDonationData() = runCoroutineTest {
        model.getDonationData(parser, center.tainan().id).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getDonationDataCache() = runCoroutineTest {
        repeat(2) {
            model.getDonationData(parser).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun getDonationDataCity() = runCoroutineTest {
        model.changeCity(center.taichung().id)
        model.city.asLiveData().observeForever { }
        model.getDonationData(parser).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun daysList() = runCoroutineTest {
        model.getDonationData(parser, center.taichung().id)
        model.daysList.asLiveData().observeForever { days ->
            Assert.assertTrue(days.isNotEmpty())
            Assert.assertEquals(7, days.size)
        }
    }

    @Test
    fun getSpotListData() = runCoroutineTest {
        model.getSpotListData(parser, center.kaohsiung().id).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getSpotListDataCache() = runCoroutineTest {
        repeat(2) {
            model.getSpotListData(parser).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun getSpotListDataCity() = runCoroutineTest {
        model.changeCity(center.taipei().id)
        model.city.asLiveData().observeForever { }
        model.getSpotListData(parser).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun spotList() = runCoroutineTest {
        model.getSpotListData(parser, center.kaohsiung().id)
        model.spotList.asLiveData().observeForever { list ->
            Assert.assertTrue(list.isNotEmpty())
        }
    }

    @Test
    fun changeCity() = runCoroutineTest {
        model.changeCity(center.hsinchu().id)
        model.city.asLiveData().observeForever { id ->
            Assert.assertEquals(center.hsinchu().id, id)
        }
    }
}