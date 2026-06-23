package io.github.liukscot.sonari.audio

import org.junit.Assert.assertEquals
import org.junit.Test

class MixStoreTest {

    @Test fun roundTrips() {
        val mix = mapOf("rain" to 0.5f, "waves" to 0.25f)
        assertEquals(mix, MixStore.parseVolumes(MixStore.encodeVolumes(mix)))
    }

    @Test fun emptyAndNullAreEmptyMaps() {
        assertEquals(emptyMap<String, Float>(), MixStore.parseVolumes(null))
        assertEquals(emptyMap<String, Float>(), MixStore.parseVolumes(""))
    }

    @Test fun dropsMalformedEntries() {
        // no '=', empty id, non-numeric value -> dropped; valid one kept.
        assertEquals(
            mapOf("rain" to 0.5f),
            MixStore.parseVolumes("rain=0.5;bad;=0.3;x=;y=abc"),
        )
    }
}
