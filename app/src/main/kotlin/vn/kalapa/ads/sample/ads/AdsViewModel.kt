package vn.kalapa.ads.sample.ads

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.kalapa.ads.controller.AdController
import vn.kalapa.ads.core.result.SdkResult
import vn.kalapa.ads.sample.data.EventLog
import vn.kalapa.ads.sample.data.MiniAppProfile
import vn.kalapa.ads.sample.data.Scenario
import vn.kalapa.ads.sample.data.ScenarioTransport
import vn.kalapa.ads.sample.data.SampleEvent
import vn.kalapa.ads.sample.data.miniAppProfileStore
import vn.kalapa.ads.session.AdsSession

/** What the screens show. [live] and [scenario] pick where the ads come from; [slots] are keyed by placement. */
internal data class SampleUiState(
    val live: Boolean = false,
    val scenario: Scenario = Scenario.BANNER,
    val session: AdsSession? = null,
    val slots: Map<String, AdController> = emptyMap(),
    val pendingOpen: PendingMiniApp? = null,
)

private const val NOTIFICATION_ENTRY = "notification"

/** The SDK's `MINIAPP_ALREADY_OPEN` error code (3004). */
private const val ALREADY_OPEN_CODE = 3004
private const val MAX_ALREADY_OPEN_RETRIES = 5
private const val ALREADY_OPEN_RETRY_MS = 300L

/** A mini-app a notification tap asked for and that is still waiting to open. A null [url] is the default URL. */
internal data class PendingMiniApp(val url: String?)

/**
 * Owns the SDK session and the ad controllers, so a configuration change neither restarts the session nor reloads
 * the ads. The session is rebuilt when Live swaps the transport or an applied profile changes; the offline scenario
 * can change under a running session.
 */
internal class AdsViewModel(private val application: Application) : AndroidViewModel(application) {
    private val eventLog = EventLog()
    private val profileStore = miniAppProfileStore(application)
    private val transport = ScenarioTransport(application.assets)
    private val mutableState = MutableStateFlow(SampleUiState())
    private val seenEventId = MutableStateFlow(0L)
    private var profile = profileStore.load()
    private var slotWidthDp = 0

    // Bumped on every restart, so a session that finishes starting after a newer restart is closed, not used.
    private var generation = 0

    val state: StateFlow<SampleUiState> = mutableState.asStateFlow()

    /** What the SDK reported to the telemetry sink, newest first. */
    val events: StateFlow<List<SampleEvent>> get() = eventLog.events

    /** Events newer than the last ones the Events tab showed; counted by id, because the log itself is capped. */
    val unreadEvents: StateFlow<Int> = combine(eventLog.events, seenEventId) { events, seen ->
        events.count { it.id > seen }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** The profile saved on this device; the settings form starts from it. */
    val savedProfile: MiniAppProfile get() = profile

    init {
        restartSession()
    }

    fun clearEvents() = eventLog.clear()

    /** Marks every event on the list as seen. Ids grow, but two callbacks racing may insert out of order. */
    fun markEventsSeen() {
        seenEventId.value = eventLog.events.value.maxOfOrNull { it.id } ?: seenEventId.value
    }

    fun setLive(live: Boolean) {
        mutableState.update { it.copy(live = live) }
        restartSession()
    }

    fun setScenario(scenario: Scenario) {
        // Before the reload: the transport must already answer with the new scenario.
        transport.scenario = scenario
        mutableState.update { it.copy(scenario = scenario) }
        loadSlots()
    }

    fun reload() = loadSlots()

    /** The feed reports how wide an ad may be; the controllers are created once the session and the width are known. */
    fun onSlotWidth(widthDp: Int) {
        if (widthDp == slotWidthDp) return
        slotWidthDp = widthDp
        createSlots()
    }

    fun saveProfile(profile: MiniAppProfile) = profileStore.save(profile)

    fun applyProfile(profile: MiniAppProfile) {
        profileStore.save(profile)
        this.profile = profile
        restartSession()
    }

    fun clearProfile() {
        profileStore.clear()
        profile = MiniAppProfile()
        restartSession()
    }

    /**
     * SDK step 5: opens the mini-app with no ad on screen and logs how it ended. Runs in [viewModelScope], not an
     * Activity scope: the mini-app screen outlives the Activity that asked for it. A null [url] opens the default
     * production URL. The URL is not logged: the SDK adds the profile's identity fields to it.
     */
    fun openMiniApp(url: String?) = launchMiniApp(url, entry = if (url == null) "default_url" else "uat_url")

    /**
     * A notification tap asked for a mini-app. A cold start has no session yet and no resumed Activity, which the
     * SDK needs to present the screen, so the request waits in the state until [openPending] runs.
     */
    fun openFromNotification(url: String?) = mutableState.update { it.copy(pendingOpen = PendingMiniApp(url)) }

    /** Opens what [openFromNotification] kept, once. The caller waits for the session and a resumed Activity. */
    fun openPending() {
        val pending = state.value.pendingOpen ?: return
        if (state.value.session == null) return
        mutableState.update { it.copy(pendingOpen = null) }
        launchMiniApp(pending.url, entry = NOTIFICATION_ENTRY)
    }

    private fun launchMiniApp(url: String?, entry: String) {
        val current = state.value.session ?: return
        viewModelScope.launch {
            var opened = current.openMiniApp(url)
            // A tap while a mini-app is open finishes that screen (the notification Intent uses CLEAR_TOP), and it is
            // still closing when this call starts: the SDK allows one mini-app at a time, so wait for it to go.
            var retries = 0
            while (entry == NOTIFICATION_ENTRY && (opened as? SdkResult.Failure)?.error?.code == ALREADY_OPEN_CODE &&
                retries++ < MAX_ALREADY_OPEN_RETRIES
            ) {
                delay(ALREADY_OPEN_RETRY_MS)
                opened = current.openMiniApp(url)
            }
            when (val result = opened) {
                is SdkResult.Success -> eventLog.onEvent(
                    "mini_app_outcome",
                    buildMap {
                        put("entry", entry)
                        put("close_reason", result.value.closeReason.name)
                        result.value.reportedResult?.let { put("result", it.name) }
                    },
                )

                is SdkResult.Failure -> eventLog.onEvent(
                    "mini_app_failed",
                    mapOf("entry" to entry, "error_code" to result.error.code.toString()),
                )
            }
        }
    }

    private fun restartSession() {
        val mine = ++generation
        releaseSession()
        viewModelScope.launch {
            val started = startAdsSession(
                application,
                transport = if (state.value.live) null else transport,
                profile = profile,
                telemetry = eventLog,
            )
            if (mine != generation) {
                started?.close()
                return@launch
            }
            mutableState.update { it.copy(session = started) }
            createSlots()
        }
    }

    private fun createSlots() {
        val session = state.value.session ?: return
        if (slotWidthDp == 0) return
        disposeSlots()
        mutableState.update { it.copy(slots = createSlotControllers(session, slotWidthDp)) }
        loadSlots()
    }

    /** SDK step 3: you decide when to load. */
    private fun loadSlots() {
        state.value.slots.values.forEach { controller -> viewModelScope.launch { controller.load() } }
    }

    private fun disposeSlots() {
        state.value.slots.values.forEach(AdController::dispose)
        mutableState.update { it.copy(slots = emptyMap()) }
    }

    private fun releaseSession() {
        disposeSlots()
        state.value.session?.close()
        mutableState.update { it.copy(session = null) }
    }

    override fun onCleared() {
        generation++
        releaseSession()
    }
}
