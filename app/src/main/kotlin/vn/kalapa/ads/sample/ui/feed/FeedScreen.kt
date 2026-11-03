package vn.kalapa.ads.sample.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import vn.kalapa.ads.ad.AdCreative
import vn.kalapa.ads.adsui.banner.BannerAdView
import vn.kalapa.ads.adsui.nativead.NativeAdView
import vn.kalapa.ads.controller.AdController
import vn.kalapa.ads.controller.AdControllerState
import vn.kalapa.ads.controller.AdControllerState.Phase
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.ads.PLACEMENT_FEED_BOTTOM
import vn.kalapa.ads.sample.ads.PLACEMENT_FEED_TOP
import vn.kalapa.ads.sample.ui.theme.SampleSizes
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

private const val POST_COUNT = 12

/** The space above and below an ad slot; the SDK offers `@dimen/kalapa_ads_ad_margin` for the same 8dp. */
private val SlotMargin = PaddingValues(vertical = SampleSpacing.One)

/** Where each slot sits in the feed: after the third post, and after the tenth, so one scrolls out of view. */
private val slotAfterPost = mapOf(2 to PLACEMENT_FEED_TOP, 9 to PLACEMENT_FEED_BOTTOM)

/** The feed's posts with the ad [slots] between them. */
@Composable
internal fun FeedScreen(
    slots: Map<String, AdController>,
    onSlotWidth: (widthDp: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The slot is the feed's width less the list padding.
    val windowWidthDp = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp().value.toInt() }
    LaunchedEffect(windowWidthDp) { onSlotWidth(windowWidthDp - 2 * FEED_PADDING_DP) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(SampleSpacing.Two),
        verticalArrangement = Arrangement.spacedBy(SampleSpacing.One),
    ) {
        // Posts and slots are separate items so the list spacing applies between them; the ad's own margin adds to it.
        repeat(POST_COUNT) { index ->
            item(key = "post$index") { Post(index + 1) }
            slotAfterPost[index]?.let { placement ->
                slots[placement]?.let { item(key = placement) { Slot(placement, it) } }
            }
        }
    }
}

private const val FEED_PADDING_DP = 16

@Composable
private fun Post(number: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(SampleSpacing.Two), verticalArrangement = Arrangement.spacedBy(SampleSpacing.Half)) {
            Text(stringResource(R.string.post_title, number), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.post_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** One placement: the banner when there is one, a visible stand-in when there is not, and its state below. */
@Composable
private fun Slot(placement: String, controller: AdController) {
    val state by controller.state.collectAsStateWithLifecycle()
    Column {
        when (state.phase) {
            Phase.Ready -> {
                // SDK step 4: the SDK views are Android Views, so a Compose host embeds them with AndroidView (a
                // Views host declares them in XML). Each view draws only its own kind of creative, so the slot picks
                // the one that matches what the backend sent, and composes it only when the ad is Ready: the view
                // hides itself until then, but padding around it would stay.
                // A new controller must get a new view: the view is bound to its controller only once, in factory.
                if (state.ad?.creative is AdCreative.Banner) {
                    key(controller) {
                        AndroidView(
                            factory = { BannerAdView(it).apply { setController(controller) } },
                            modifier = Modifier.fillMaxWidth().padding(SlotMargin),
                        )
                    }
                } else {
                    key(controller) {
                        AndroidView(
                            factory = { NativeAdView(it).apply { setController(controller) } },
                            modifier = Modifier.fillMaxWidth().padding(SlotMargin),
                        )
                    }
                }
            }

            Phase.Idle, Phase.Loading -> LinearProgressIndicator(
                Modifier.fillMaxWidth().padding(SlotMargin),
            )

            else -> EmptySlot(state)
        }
        StatePill(placement, state)
    }
}

@Composable
private fun EmptySlot(state: AdControllerState) {
    val detail = state.noFillReason ?: state.error?.let { "#${it.code}" }
    Box(
        modifier = Modifier
            .padding(SlotMargin)
            .fillMaxWidth()
            .heightIn(min = SampleSizes.MinTouchTarget + SampleSpacing.Two)
            .clip(RoundedCornerShape(SampleSpacing.One))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.slot_empty, detail ?: state.phase.name),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(SampleSpacing.One),
        )
    }
}

@Composable
private fun StatePill(placement: String, state: AdControllerState) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (state.phase) {
        Phase.Ready -> scheme.primaryContainer to scheme.onPrimaryContainer
        Phase.NoFill -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        Phase.Failed -> scheme.errorContainer to scheme.onErrorContainer
        else -> scheme.secondaryContainer to scheme.onSecondaryContainer
    }
    Text(
        text = stringResource(R.string.slot_status, placement, state.phase.name),
        style = MaterialTheme.typography.labelSmall,
        color = content,
        modifier = Modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = SampleSpacing.One, vertical = SampleSpacing.Half),
    )
}
