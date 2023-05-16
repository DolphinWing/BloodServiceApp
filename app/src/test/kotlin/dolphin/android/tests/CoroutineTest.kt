package dolphin.android.tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Basic implementation to setup/clean Coroutine test.
 * See https://www.wwt.com/article/testing-android-datastore
 * https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test
 */
@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
abstract class CoroutineTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setupDispatcherScope() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun cleanupDispatcherScope() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    fun runCoroutineTest(block: suspend CoroutineScope.() -> Unit) = runTest(testBody = block)
//        runBlocking {
//            launch(Dispatchers.Main, block = block)
//        }
}
