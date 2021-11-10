package dolphin.android.apps.BloodServiceApp.provider

import android.os.Build
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import dolphin.android.tests.DataStoreTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * See [https://www.wwt.com/article/testing-android-datastore]
 */
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.R])
@RunWith(AndroidJUnit4::class)
class PrefsDataStoreTest : DataStoreTest() {
    private lateinit var prefs: PrefsDataStore

    @Before
    fun setup() {
        prefs = PrefsDataStore(dataStore = dataStore)
    }

    @Test
    fun `change to taipei`() = runCoroutuneTest {
        prefs.changeCenter(2)
        prefs.centerId.asLiveData().observeForever { actual ->
            Assert.assertEquals(2, actual)
        }
    }

    @Test
    fun `change to hsinchu`() = runCoroutuneTest {
        prefs.changeCenter(3)
        prefs.centerId.asLiveData().observeForever { actual ->
            Assert.assertEquals(3, actual)
        }
    }

    @Test
    fun `policy code default`() = runCoroutuneTest {
        prefs.policyCode.asLiveData().observeForever { actual ->
            Assert.assertEquals(0, actual)
        }
    }

    @Test
    fun `change policy code`() = runCoroutuneTest {
        prefs.updatePolicyCode(20211110)
        prefs.policyCode.asLiveData().observeForever { actual ->
            Assert.assertEquals(20211110, actual)
        }
    }
}
