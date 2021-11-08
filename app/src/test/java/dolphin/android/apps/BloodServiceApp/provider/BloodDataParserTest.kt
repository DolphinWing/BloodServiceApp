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
class BloodDataParserTest {
    private lateinit var parser: BloodDataParser
    private lateinit var reader: BloodDataReaderTestImpl
    private lateinit var center: BloodCenter

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        reader = BloodDataReaderTestImpl(context)
        parser = BloodDataParser(context, reader)
        center = BloodCenter(context)
    }

    @Test
    fun `read blood storage content`() {
        val html = reader.readBloodStorage()
        Assert.assertTrue(html.contains("tool_blood_cube"))
        Assert.assertTrue(html.contains("tool_danger"))
    }

    @Test
    fun `read blood storage pass`() {
        val storage1 = parser.getBloodStorage(false)
        Assert.assertEquals(5, storage1.size())

        reader.contentAsset = "" // read a empty file to make it no response data
        val storage2 = parser.getBloodStorage(false)
        Assert.assertEquals(5, storage2.size())

        val storage3 = parser.getBloodStorage(true)
        Assert.assertEquals(0, storage3.size())
    }

    @Test
    fun `read blood storage failed`() {
        reader.contentAsset = "" // read a empty file to make it no response data
        val storage = parser.getBloodStorage(true)
        Assert.assertEquals(0, storage.size())
    }

    @Test
    fun `read tainan this week`() {
        val days = parser.getWeekCalendar(center.tainan().id)
        Assert.assertEquals(7, days.size)
    }

    @Test
    fun `read taipei this week`() {
        val days = parser.getWeekCalendar(center.taipei().id)
        Assert.assertEquals(7, days.size)
    }

    @Test
    fun `read tainan recent days`() {
        val days = parser.getLatestWeekCalendar(center.tainan().id)
        Assert.assertEquals(7, days.size)
    }

    @Test
    fun `read taipei recent days`() {
        val days = parser.getLatestWeekCalendar(center.taipei().id)
        Assert.assertEquals(7, days.size)
    }

    @Test
    fun `read Taipei spot list`() {
        // reader.contentAsset = "location_map_2_13.txt" // test with the same file
        val (order, maps) = parser.getDonationSpotLocationMap(center.taipei().id)
        Assert.assertEquals(5, order.size)
        Assert.assertEquals(5, maps.size())
    }

    @Test
    fun `read Taichung spot list`() {
        // reader.contentAsset = "location_map_4_19.txt" // test with the same file
        val (order, maps) = parser.getDonationSpotLocationMap(center.taichung().id)
        Assert.assertEquals(4, order.size)
        Assert.assertEquals(4, maps.size())
    }

    @Test
    fun `read Kaohsiung spot list`() {
        // reader.contentAsset = "location_map_6_27.txt" // test with the same file
        val (order, maps) = parser.getDonationSpotLocationMap(center.kaohsiung().id)
        Assert.assertEquals(4, order.size)
        Assert.assertEquals(4, maps.size())
    }
}