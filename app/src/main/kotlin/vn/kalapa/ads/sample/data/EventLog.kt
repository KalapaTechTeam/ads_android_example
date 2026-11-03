package vn.kalapa.ads.sample.data

import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import vn.kalapa.ads.core.telemetry.TelemetrySink

internal class SampleEvent(
    val id: Long,
    val timeMillis: Long,
    val name: String,
    val attributes: Map<String, String>,
)

/** What the SDK reported to the host's telemetry sink, newest first; the SDK events tab shows it. */
internal class EventLog : TelemetrySink {
    private val state = MutableStateFlow<List<SampleEvent>>(emptyList())
    private val nextId = AtomicLong()

    val events: StateFlow<List<SampleEvent>> get() = state

    override fun onEvent(name: String, attributes: Map<String, String>) {
        val event = SampleEvent(nextId.incrementAndGet(), System.currentTimeMillis(), name, attributes)
        state.update { (listOf(event) + it).take(MAX_EVENTS) }
    }

    fun clear() {
        state.value = emptyList()
    }

    private companion object {
        const val MAX_EVENTS = 200
    }
}
