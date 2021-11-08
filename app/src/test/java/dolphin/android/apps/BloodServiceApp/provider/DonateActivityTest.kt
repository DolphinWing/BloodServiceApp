package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dolphin.android.apps.BloodServiceApp.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.Locale

@Config(sdk = [Build.VERSION_CODES.R])
@RunWith(AndroidJUnit4::class)
class DonateActivityTest {
    private lateinit var context: Context
    private lateinit var calendar: Calendar

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        calendar = Calendar.getInstance(Locale.TAIWAN)
        calendar.set(2021, Calendar.OCTOBER, 20)
    }

    private fun getString(@StringRes id: Int): String = context.getString(id)

    private fun getStringArray(id: Int) = context.resources.getStringArray(id)

    private fun Calendar.setTime(
        refDay: Calendar = calendar,
        @IntRange(from = 0, to = 23) hourOfDay: Int = 8,
        @IntRange(from = 0, to = 59) minute: Int = 0,
    ) {
        timeInMillis = refDay.timeInMillis // set reference day
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    @Test
    fun `equal test - not equal`() {
        val event1 = DonateActivity("AAA", "BBB").apply {
            startTime.setTime(hourOfDay = 8)
            endTime.setTime(hourOfDay = 17)
        }
        val event2 = DonateActivity("BBB", "CCC").apply {
            startTime.setTime(hourOfDay = 8)
            endTime.setTime(hourOfDay = 17)
        }
        Assert.assertNotEquals(event1, event2)
        Assert.assertNotEquals(event1.hashCode(), event2.hashCode())
        Assert.assertNotEquals(event1.toString(), event2.toString())
    }

    @Test
    fun `equal test - equal`() {
        val event1 = DonateActivity("ABC", "somewhere").apply {
            startTime.setTime(hourOfDay = 8)
            endTime.setTime(hourOfDay = 17)
        }
        val event2 = DonateActivity("ABC", "somewhere").apply {
            startTime.setTime(hourOfDay = 8)
            endTime.setTime(hourOfDay = 17)
        }
        Assert.assertEquals(event1, event2)
        Assert.assertEquals(event1.hashCode(), event2.hashCode())
        Assert.assertEquals(event1.toString(), event2.toString())
    }

    @Test
    fun `duration parse 01`() {
        val event = DonateActivity("unknown", "nowhere")
        event.setDuration(calendar, "0900~1600")
        Assert.assertEquals("09:00", event.startTimeString)
        Assert.assertEquals("16:00", event.endTimeString)
    }

    @Test
    fun `duration parse 02`() {
        val event = DonateActivity("unknown", "nowhere")
        event.setDuration(calendar, "1000-1800")
        Assert.assertEquals("10:00", event.startTimeString)
        Assert.assertEquals("18:00", event.endTimeString)
    }

    @Test
    fun `duration parse 03`() {
        val event = DonateActivity("unknown", "nowhere")
        event.setDuration(calendar, "9:30~16:30")
        Assert.assertEquals("09:30", event.startTimeString)
        Assert.assertEquals("16:30", event.endTimeString)
    }

    @Test
    fun `duration parse 04`() {
        val event = DonateActivity("unknown", "nowhere")
        event.setDuration(calendar, "0930")
        Assert.assertEquals("09:30", event.startTimeString)
        Assert.assertEquals("17:00", event.endTimeString)
    }

    @Test
    fun `duration parse 05`() {
        val event = DonateActivity("unknown", "nowhere")
        event.setDuration(calendar, "0930 1630")
        Assert.assertEquals("09:30", event.startTimeString)
        Assert.assertEquals("16:30", event.endTimeString)
    }

    @Test
    fun `map search 00`() {
        val event = DonateActivity("someone", "somewhere")
        val list = event.prepareLocationList(context)
        Assert.assertEquals(2, list.size)
    }

    @Test
    fun `map search name 01`() {
        val event = DonateActivity(getString(R.string.paren_test_name_01), "somewhere")
        val list = event.prepareLocationList(context)
        Assert.assertEquals(4, list.size)

        val result = getStringArray(R.array.paren_test_name_01_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search name 02`() {
        val event = DonateActivity(getString(R.string.paren_test_name_02), "nowhere")
        val list = event.prepareLocationList(context)
        Assert.assertEquals(4, list.size)

        val result = getStringArray(R.array.paren_test_name_02_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 01`() {
        val event = DonateActivity("someone", getString(R.string.paren_test_location_01))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(3, list.size)

        val result = getStringArray(R.array.paren_test_location_01_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 02`() {
        val event = DonateActivity("no one", getString(R.string.paren_test_location_02))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(3, list.size)

        val result = getStringArray(R.array.paren_test_location_02_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 03`() {
        val event = DonateActivity("anyone", getString(R.string.paren_test_location_03))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(3, list.size)

        val result = getStringArray(R.array.paren_test_location_03_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 04`() {
        val event = DonateActivity("004", getString(R.string.paren_test_location_04))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(3, list.size)

        val result = getStringArray(R.array.paren_test_location_04_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 05`() {
        val event = DonateActivity("005", getString(R.string.paren_test_location_05))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(3, list.size)

        val result = getStringArray(R.array.paren_test_location_05_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 06`() {
        val event = DonateActivity("006", getString(R.string.paren_test_location_06))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(3, list.size)

        val result = getStringArray(R.array.paren_test_location_06_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }

    @Test
    fun `map search location 07`() {
        val event = DonateActivity("007", getString(R.string.paren_test_location_07))
        val list = event.prepareLocationList(context)
        Assert.assertEquals(4, list.size)

        val result = getStringArray(R.array.paren_test_location_07_result)
        Assert.assertEquals(result.size, list.size)
        result.forEachIndexed { index, expected ->
            Assert.assertEquals(expected, list[index])
        }
    }
}