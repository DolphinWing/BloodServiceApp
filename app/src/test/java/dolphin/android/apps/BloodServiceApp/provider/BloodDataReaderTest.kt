package dolphin.android.apps.BloodServiceApp.provider

import androidx.test.filters.LargeTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@LargeTest
class BloodDataReaderTest {
    private lateinit var server: MockWebServer
    private lateinit var reader: BloodDataReaderImpl

    class BloodDataReaderImplTester(private val mocked: MockWebServer) : BloodDataReaderImpl(1) {
        override fun body(url: String): String {
            mocked.start()
            return super.body(mocked.url("mock-url").toString())
        }
    }

    @Before
    fun setup() {
        server = MockWebServer()
        reader = BloodDataReaderImplTester(server)
    }

    @After
    fun clean() {
        server.shutdown()
    }

    @Test
    fun warmUp() {
        server.enqueue(MockResponse().setBody("ok").setResponseCode(200))
        reader.warmUp()
    }

    @Test
    fun warmFailed() {
        // server.enqueue(MockResponse().setResponseCode(404))
        reader.warmUp()
    }

    @Test
    fun bodyTimeout() {
        // server.enqueue(MockResponse().setResponseCode(404))
        Assert.assertEquals("(io)", reader.readBloodStorage())
    }

    @Test
    fun bodyNull() {
        server.enqueue(MockResponse().setResponseCode(201))
        Assert.assertEquals("", reader.readBloodStorage())
    }

    @Test
    fun readBloodStorage() {
        server.enqueue(MockResponse().setBody("ok").setResponseCode(200))
        Assert.assertTrue(reader.readBloodStorage().isNotEmpty())
    }

    @Test
    fun readWeekCalendar() {
        server.enqueue(MockResponse().setBody("ok").setResponseCode(200))
        Assert.assertTrue(reader.readWeekCalendar(0, Calendar.getInstance()).isNotEmpty())
    }

    @Test
    fun readDonationSpotList() {
        server.enqueue(MockResponse().setBody("ok").setResponseCode(200))
        Assert.assertTrue(reader.readDonationSpotList(0, 0, "test").isNotEmpty())
    }
}
