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
    fun ready() = runCoroutuneTest {
        model.ready.observeForever { actual ->
            Assert.assertFalse(actual)
        }
    }

    @Test
    fun postReady() = runCoroutuneTest {
        model.ready.postValue(true)
        model.ready.observeForever { actual ->
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun darkMode() = runCoroutuneTest {
        model.darkMode.asLiveData().observeForever { actual ->
            Assert.assertFalse(actual)
        }
    }

    @Test
    fun changeDarkMode() = runCoroutuneTest {
        model.darkMode.emit(true)
        model.darkMode.asLiveData().observeForever { actual ->
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun noPrivacyUpdate() = runCoroutuneTest {
        model.showPrivacyReview.asLiveData().observeForever { actual ->
            Assert.assertFalse(actual)
        }
    }

    @Test
    fun reviewPrivacy() = runCoroutuneTest {
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
    fun changeUiState() = runCoroutuneTest {
        model.changeUiState(UiState.Welcome)
        model.uiState.observeForever { actual ->
            Assert.assertEquals(UiState.Welcome, actual)
            Assert.assertEquals(actual, model.state)
        }
    }

    @Test
    fun warmUp() = runCoroutuneTest {
        model.init(parser).asLiveData().observeForever { actual ->
            Assert.assertTrue(actual) // always true
        }
    }

    @Test
    fun getStorageData() = runCoroutuneTest {
        model.getStorageData(parser).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getStorageDataRefresh() = runCoroutuneTest {
        model.getStorageData(parser, true).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getStorageDataCache() = runCoroutuneTest {
        repeat(2) {
            model.getStorageData(parser).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun getStorageDataCacheRefresh() = runCoroutuneTest {
        repeat(2) {
            model.getStorageData(parser, true).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun storageMap() = runCoroutuneTest {
        model.getStorageData(parser, centerId = center.taipei().id)
        model.storageMap.asLiveData().observeForever { map ->
            Assert.assertTrue(map.isNotEmpty())
        }
    }

    @Test
    fun getDonationData() = runCoroutuneTest {
        model.getDonationData(parser, center.tainan().id).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getDonationDataCache() = runCoroutuneTest {
        repeat(2) {
            model.getDonationData(parser).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun getDonationDataCity() = runCoroutuneTest {
        model.changeCity(center.taichung().id)
        model.city.asLiveData().observeForever { }
        model.getDonationData(parser).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun daysList() = runCoroutuneTest {
        model.getDonationData(parser, center.taichung().id)
        model.daysList.asLiveData().observeForever { days ->
            Assert.assertTrue(days.isNotEmpty())
            Assert.assertEquals(7, days.size)
        }
    }

    @Test
    fun getSpotListData() = runCoroutuneTest {
        model.getSpotListData(parser, center.kaohsiung().id).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun getSpotListDataCache() = runCoroutuneTest {
        repeat(2) {
            model.getSpotListData(parser).asLiveData().observeForever { result ->
                Assert.assertFalse(result)
            }
        }
    }

    @Test
    fun getSpotListDataCity() = runCoroutuneTest {
        model.changeCity(center.taipei().id)
        model.city.asLiveData().observeForever { }
        model.getSpotListData(parser).asLiveData().observeForever { result ->
            Assert.assertFalse(result)
        }
    }

    @Test
    fun spotList() = runCoroutuneTest {
        model.getSpotListData(parser, center.kaohsiung().id)
        model.spotList.asLiveData().observeForever { list ->
            Assert.assertTrue(list.isNotEmpty())
        }
    }

    @Test
    fun changeCity() = runCoroutuneTest {
        model.changeCity(center.hsinchu().id)
        model.city.asLiveData().observeForever { id ->
            Assert.assertEquals(center.hsinchu().id, id)
        }
    }
}