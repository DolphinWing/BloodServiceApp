package dolphin.android.tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Basic implementation to setup/clean Coroutine test.
 * See https://www.wwt.com/article/testing-android-datastore
 */
@ExperimentalCoroutinesApi
abstract class CoroutineTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Suppress("MemberVisibilityCanBePrivate")
    protected val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @Suppress("MemberVisibilityCanBePrivate")
    protected val testCoroutineScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setupDispatcherScope() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanupDispatcherScope() {
        Dispatchers.resetMain()
    }

    @After
    fun cleanupCoroutines() {
        testDispatcher.cleanupTestCoroutines()
        testDispatcher.resumeDispatcher()
    }

    fun runCoroutuneTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest(block)
}
