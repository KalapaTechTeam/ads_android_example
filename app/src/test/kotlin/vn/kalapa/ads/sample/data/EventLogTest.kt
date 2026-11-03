package vn.kalapa.ads.sample.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventLogTest {
    @Test
    fun newestEventComesFirstAndKeepsItsAttributes() {
        val log = EventLog()

        log.onEvent("first", emptyMap())
        log.onEvent("second", mapOf("placement_id" to "feed_top"))

        assertEquals(listOf("second", "first"), log.events.value.map { it.name })
        assertEquals("feed_top", log.events.value.first().attributes["placement_id"])
    }

    @Test
    fun keepsOnlyTheLatestTwoHundredEvents() {
        val log = EventLog()

        repeat(250) { log.onEvent("event_$it", emptyMap()) }

        assertEquals(200, log.events.value.size)
        assertEquals("event_249", log.events.value.first().name)
        assertEquals("event_50", log.events.value.last().name)
    }

    @Test
    fun clearEmptiesTheLog() {
        val log = EventLog()
        log.onEvent("event", emptyMap())

        log.clear()

        assertTrue(log.events.value.isEmpty())
    }
}
