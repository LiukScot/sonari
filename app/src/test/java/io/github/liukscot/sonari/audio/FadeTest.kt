package io.github.liukscot.sonari.audio

import org.junit.Assert.assertEquals
import org.junit.Test

class FadeTest {
    @Test fun startsAtZero() = assertEquals(0f, Fade.factorAt(0, 1000), 0f)

    @Test fun endsAtOne() = assertEquals(1f, Fade.factorAt(1000, 1000), 0f)

    @Test fun halfwayIsHalf() = assertEquals(0.5f, Fade.factorAt(500, 1000), 1e-4f)

    @Test fun clampsPastDuration() = assertEquals(1f, Fade.factorAt(5000, 1000), 0f)

    @Test fun zeroDurationIsInstant() = assertEquals(1f, Fade.factorAt(0, 0), 0f)
}
