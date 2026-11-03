package vn.kalapa.ads.sample.data

import android.content.res.AssetManager
import androidx.annotation.StringRes
import vn.kalapa.ads.sample.R
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import vn.kalapa.ads.transport.AdsHttpRequest
import vn.kalapa.ads.transport.AdsHttpResponse
import vn.kalapa.ads.transport.AdsTransport

/** How the scenarios are grouped in the menu. */
internal enum class ScenarioGroup(@StringRes val title: Int) {
    NORMAL(R.string.group_normal),
    BAD_DATA(R.string.group_bad_data),
    NETWORK(R.string.group_network),
    LONGEST_TEXT(R.string.group_longest_text),
}

/**
 * What the offline ads service answers. Each one is a case the SDK has to handle without a live backend; [label]
 * names it and [hint] says what the slot should do.
 */
internal enum class Scenario(
    @StringRes val label: Int,
    @StringRes val hint: Int,
    val group: ScenarioGroup,
    val fixture: String?,
) {
    BANNER(R.string.scenario_banner, R.string.hint_banner, ScenarioGroup.NORMAL, "recommendations/banner-live.json"),
    NATIVE_STRIP(
        R.string.scenario_native_strip,
        R.string.hint_native_strip,
        ScenarioGroup.NORMAL,
        "recommendations/native-strip.json",
    ),
    NATIVE_PROMO(
        R.string.scenario_native_promo,
        R.string.hint_native_promo,
        ScenarioGroup.NORMAL,
        "recommendations/native-promo.json",
    ),
    NATIVE_PRODUCT(
        R.string.scenario_native_product,
        R.string.hint_native_product,
        ScenarioGroup.NORMAL,
        "recommendations/native-product.json",
    ),
    FIRST_INVALID(
        R.string.scenario_first_invalid,
        R.string.hint_first_invalid,
        ScenarioGroup.BAD_DATA,
        "recommendations/banner-first-invalid-second-valid.json",
    ),
    NO_FILL(R.string.scenario_no_fill, R.string.hint_no_fill, ScenarioGroup.BAD_DATA, "recommendations/empty-ads.json"),
    ALL_INVALID(
        R.string.scenario_all_invalid,
        R.string.hint_all_invalid,
        ScenarioGroup.BAD_DATA,
        "recommendations/all-invalid.json",
    ),
    MALFORMED(
        R.string.scenario_malformed,
        R.string.hint_malformed,
        ScenarioGroup.BAD_DATA,
        "recommendations/not-an-object.json",
    ),
    SERVER_ERROR(R.string.scenario_server_error, R.string.hint_server_error, ScenarioGroup.NETWORK, null),
    NETWORK_ERROR(R.string.scenario_network_error, R.string.hint_network_error, ScenarioGroup.NETWORK, null),
    SLOW(R.string.scenario_slow, R.string.hint_slow, ScenarioGroup.NETWORK, "recommendations/banner-live.json"),
    LONG_STRIP(
        R.string.scenario_long_strip,
        R.string.hint_long_text,
        ScenarioGroup.LONGEST_TEXT,
        "recommendations/native-long-text-strip.json",
    ),
    LONG_PROMO(
        R.string.scenario_long_promo,
        R.string.hint_long_text,
        ScenarioGroup.LONGEST_TEXT,
        "recommendations/native-long-text-promo.json",
    ),
    LONG_PRODUCT(
        R.string.scenario_long_product,
        R.string.hint_long_text,
        ScenarioGroup.LONGEST_TEXT,
        "recommendations/native-long-text-product.json",
    ),
}

/**
 * The SDK's [AdsTransport] answering from bundled sample responses (assets), so the sample runs without the ads API.
 * The creative itself and the mini-app page are still fetched from the network by their web views.
 */
internal class ScenarioTransport(private val assets: AssetManager) : AdsTransport {

    @Volatile
    var scenario: Scenario = Scenario.BANNER

    override suspend fun execute(request: AdsHttpRequest): AdsHttpResponse {
        // Events go to the same transport; the sample accepts them all and shows them on the events tab.
        if (request.method == AdsHttpRequest.Method.POST) return AdsHttpResponse(201, "{}")
        val current = scenario
        return when (current) {
            Scenario.SERVER_ERROR -> AdsHttpResponse(503, "")

            Scenario.NETWORK_ERROR -> throw IOException("offline scenario")

            else -> {
                if (current == Scenario.SLOW) delay(SLOW_MILLIS)
                AdsHttpResponse(200, read(checkNotNull(current.fixture)))
            }
        }
    }

    private suspend fun read(path: String): String =
        withContext(Dispatchers.IO) { assets.open(path).bufferedReader().use { it.readText() } }

    private companion object {
        const val SLOW_MILLIS = 5_000L
    }
}
