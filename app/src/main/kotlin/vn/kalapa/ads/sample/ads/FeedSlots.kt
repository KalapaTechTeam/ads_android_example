package vn.kalapa.ads.sample.ads

import vn.kalapa.ads.ad.AdSlot
import vn.kalapa.ads.controller.AdController
import vn.kalapa.ads.core.result.SdkResult
import vn.kalapa.ads.session.AdsSession

// Replace with the ad unit ids Kalapa gives you. The API answers a banner unit with banners and a native unit with
// native cards, so the top slot uses the first and the bottom slot the second. The offline scenarios answer for any id.
private const val BANNER_AD_UNIT_ID = "b1a8a5f3-8cb0-4463-a69b-366ce610b255"
private const val NATIVE_AD_UNIT_ID = "be9abee4-ed55-4557-b9d4-a70ecbc1c6de"

internal const val PLACEMENT_FEED_TOP = "feed_top"
internal const val PLACEMENT_FEED_BOTTOM = "feed_bottom"

private val adUnitOf = mapOf(PLACEMENT_FEED_TOP to BANNER_AD_UNIT_ID, PLACEMENT_FEED_BOTTOM to NATIVE_AD_UNIT_ID)

/** The slot is as tall as the biggest template needs. */
private const val SLOT_MAX_HEIGHT_DP = 250

/**
 * SDK step 3: one ad slot is one controller. [widthDp] is the room the ad really has. The placement names the slot
 * in the SDK's events. Release every controller with `dispose()` when it is replaced or the screen goes away.
 */
internal fun createSlotControllers(session: AdsSession, widthDp: Int): Map<String, AdController> {
    val slot = AdSlot(widthDp, SLOT_MAX_HEIGHT_DP)
    return adUnitOf.mapNotNull { (placement, adUnitId) ->
        (session.createController(adUnitId, slot) as? SdkResult.Success)?.value?.let {
            it.setPlacement(placement)
            placement to it
        }
    }.toMap()
}
