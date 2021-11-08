package dolphin.android.apps.BloodServiceApp.provider

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class DonateDayTest {
    private lateinit var calendar: Calendar

    @Before
    fun setup() {
        calendar = Calendar.getInstance(Locale.TAIWAN)
        calendar.set(2021, Calendar.OCTOBER, 20)
    }

    @Test
    fun test0() {
        val day = DonateDay(emptyList())
        Assert.assertEquals(0, day.activityCount)
    }

    @Test
    fun setDateByCalendar() {
        val day = DonateDay(emptyList())
        day.setDate(calendar)
        Assert.assertTrue(day.dateString.startsWith("2021/10/20 "))
    }

    @Test
    fun setDate() {
        val day = DonateDay(emptyList())
        day.setDate(2020, Calendar.NOVEMBER, 13)
        Assert.assertTrue(day.dateString.startsWith("2020/11/13 "))
    }

    @Test
    fun isFuture() {
        val day = DonateDay(emptyList())
        day.setDate(2031, Calendar.DECEMBER, 31)
        Assert.assertTrue(day.isFuture)
    }

    @Test
    fun isPast() {
        val day = DonateDay(emptyList())
        day.setDate(2000, Calendar.JANUARY, 1)
        Assert.assertFalse(day.isFuture)
    }

    @Test
    fun isToday() {
        val day = DonateDay(emptyList())
        day.setDate(Calendar.getInstance(Locale.TAIWAN))
        Assert.assertFalse(day.isFuture)
    }

    @Test
    fun reset() {
        val now = Calendar.getInstance(Locale.TAIWAN)
        val sum = now.get(Calendar.MILLISECOND) + now.get(Calendar.SECOND)
        Assert.assertNotEquals(0, sum) // what chance can you get all zeros
        now.reset()
        Assert.assertEquals(0, now.get(Calendar.MILLISECOND))
        Assert.assertEquals(0, now.get(Calendar.SECOND))
    }

    @Test
    fun notEqual() {
        val day1 = DonateDay(emptyList())
        val day2 = DonateDay(listOf(DonateActivity("hello", "world")))
        Assert.assertNotEquals(day1, day2)
        Assert.assertNotEquals(day1.hashCode(), day2.hashCode())
        Assert.assertNotEquals(day1.toString(), day2.toString())
    }
}