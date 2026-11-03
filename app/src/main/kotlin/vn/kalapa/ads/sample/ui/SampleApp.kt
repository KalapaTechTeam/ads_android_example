package vn.kalapa.ads.sample.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.ads.AdsViewModel
import vn.kalapa.ads.sample.ui.events.EventsScreen
import vn.kalapa.ads.sample.ui.feed.ControlsDock
import vn.kalapa.ads.sample.ui.feed.FeedScreen
import vn.kalapa.ads.sample.ui.navigation.FloatingNavigationLayout
import vn.kalapa.ads.sample.ui.navigation.SampleFloatingNavBar
import vn.kalapa.ads.sample.ui.navigation.SampleNavItem
import vn.kalapa.ads.sample.ui.settings.SettingsScreen
import vn.kalapa.ads.sample.ui.theme.SampleDimens
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

private enum class Tab { FEED, EVENTS, SETTINGS }

/**
 * The shell: a Feed tab with ad slots, an SDK events tab and a settings tab, reached from a floating bar at the
 * bottom. The SDK session lives in [AdsViewModel]; the screens only render its state and report what the user did.
 */
@Composable
internal fun SampleApp(viewModel: AdsViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(Tab.FEED) }
    val unread by viewModel.unreadEvents.collectAsStateWithLifecycle()

    LaunchedEffect(tab, events.firstOrNull()?.id) { if (tab == Tab.EVENTS) viewModel.markEventsSeen() }
    val badge = if (tab == Tab.EVENTS) 0 else unread

    BackHandler(enabled = tab != Tab.FEED) { tab = Tab.FEED }
    val feedLabel = stringResource(R.string.tab_feed)
    val eventsLabel = stringResource(R.string.tab_events)
    val settingsLabel = stringResource(R.string.tab_settings)
    val items = listOf(
        SampleNavItem(
            id = Tab.FEED.name,
            selected = tab == Tab.FEED,
            onClick = { tab = Tab.FEED },
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = feedLabel,
        ),
        SampleNavItem(
            id = Tab.EVENTS.name,
            selected = tab == Tab.EVENTS,
            onClick = { tab = Tab.EVENTS },
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
            label = eventsLabel,
            badgeCount = badge,
        ),
        SampleNavItem(
            id = Tab.SETTINGS.name,
            selected = tab == Tab.SETTINGS,
            onClick = { tab = Tab.SETTINGS },
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            label = settingsLabel,
        ),
    )

    // Each tab keeps its own saved state (scroll position), so leaving a tab and coming back finds it as it was.
    val savedScreens = rememberSaveableStateHolder()
    FloatingNavigationLayout(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding(),
        navigation = {
            SampleFloatingNavBar(
                items,
                Modifier.navigationBarsPadding()
                    .padding(horizontal = SampleSpacing.Two, vertical = SampleSpacing.One)
                    .widthIn(max = SampleDimens.maxNavBarWidth),
            )
        },
    ) { content ->
        savedScreens.SaveableStateProvider(tab.name) {
            when (tab) {
                Tab.FEED -> Column(content) {
                    FeedScreen(
                        slots = state.slots,
                        onSlotWidth = viewModel::onSlotWidth,
                        modifier = Modifier.weight(1f),
                    )
                    ControlsDock(
                        scenario = state.scenario,
                        live = state.live,
                        onScenario = viewModel::setScenario,
                        onLive = viewModel::setLive,
                        onReload = viewModel::reload,
                    )
                }

                Tab.EVENTS -> EventsScreen(events, onClear = viewModel::clearEvents, modifier = content)

                Tab.SETTINGS -> SettingsScreen(
                    modifier = content,
                    savedProfile = viewModel.savedProfile,
                    miniAppReady = state.session != null,
                    onOpenMiniApp = viewModel::openMiniApp,
                    onSaveProfile = viewModel::saveProfile,
                    onApplyProfile = viewModel::applyProfile,
                    onClearProfile = viewModel::clearProfile,
                )
            }
        }
    }
}
