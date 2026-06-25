package io.github.liukscot.sonari.audio

import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SleepTimerTest {

    /** Creates a SleepTimer wired to the test scope with a fixed fake clock. */
    private fun kotlinx.coroutines.test.TestScope.makeTimer(
        now: () -> Long = { 0L },
        onExpire: () -> Unit = {},
    ) = SleepTimer(scope = this, onExpire = onExpire, now = now)

    @Test fun set_updatesStateFlows() = runTest {
        val timer = makeTimer(now = { 1_000L })
        timer.set(30)
        assertEquals(30, timer.minutes.value)
        assertEquals(1_000L + 30 * 60_000L, timer.endRealtimeMs.value)
    }

    @Test fun set_zero_isNoOp() = runTest {
        val timer = makeTimer()
        timer.set(0)
        assertEquals(0, timer.minutes.value)
        assertNull(timer.endRealtimeMs.value)
    }

    @Test fun cancel_resetsState() = runTest {
        val timer = makeTimer()
        timer.set(30)
        timer.cancel()
        assertEquals(0, timer.minutes.value)
        assertNull(timer.endRealtimeMs.value)
    }

    @Test fun expire_invokesCallbackAndResetsState() = runTest {
        var expired = false
        val timer = makeTimer(onExpire = { expired = true })
        timer.set(30)
        advanceTimeBy(30 * 60_000L + 1)
        assertTrue(expired)
        assertEquals(0, timer.minutes.value)
        assertNull(timer.endRealtimeMs.value)
    }

    @Test fun cancel_preventsExpiry() = runTest {
        var expired = false
        val timer = makeTimer(onExpire = { expired = true })
        timer.set(5)
        timer.cancel()
        advanceTimeBy(5 * 60_000L + 1)
        assertFalse(expired)
        assertEquals(0, timer.minutes.value)
    }

    @Test fun rapidReset_usesLatestDuration() = runTest {
        var expireCount = 0
        val timer = makeTimer(onExpire = { expireCount++ })
        timer.set(5)
        timer.set(10)            // replaces the 5-min job
        advanceTimeBy(5 * 60_000L + 1)
        assertEquals(0, expireCount)   // 5-min job was cancelled
        advanceTimeBy(5 * 60_000L)
        assertEquals(1, expireCount)   // 10-min job fires
    }
}
