package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.R])
@RunWith(AndroidJUnit4::class)
class BloodCenterTest {
    private lateinit var center: BloodCenter

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        center = BloodCenter(context = context)
    }

    @Test
    fun `find taipei`() {
        Assert.assertEquals(center.taipei(), center.find(2))
    }

    @Test
    fun `find hsinchu`() {
        Assert.assertEquals(center.hsinchu(), center.find(3))
    }

    @Test
    fun `find taichung`() {
        Assert.assertEquals(center.taichung(), center.find(4))
    }

//    @Test
//    fun `find tainan`() {
//        Assert.assertEquals(center.tainan(), center.find(5))
//    }

    @Test
    fun `find kaohsiung`() {
        Assert.assertEquals(center.kaohsiung(), center.find(6))
    }

    @Test
    fun `not found`() {
        Assert.assertEquals(center.main(), center.find(-1))
    }

    @Test
    fun `not equal center`() {
        Assert.assertNotEquals(center.main(), center.kaohsiung())
        Assert.assertNotEquals(center.main().hashCode(), center.kaohsiung().hashCode())
        Assert.assertNotEquals(center.main().toString(), center.kaohsiung().toString())
    }

    @Test
    fun `not equal instance`() {
        Assert.assertNotEquals(center, center.main())
    }
}
