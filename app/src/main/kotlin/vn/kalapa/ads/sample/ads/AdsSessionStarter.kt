package vn.kalapa.ads.sample.ads

import android.app.Application
import java.io.File
import vn.kalapa.ads.AdsSdk
import vn.kalapa.ads.config.AdsSdkConfig
import vn.kalapa.ads.core.environment.SdkEnvironment
import vn.kalapa.ads.core.logging.LogLevel
import vn.kalapa.ads.core.logging.LogcatSink
import vn.kalapa.ads.core.logging.SdkLogger
import vn.kalapa.ads.core.result.SdkResult
import vn.kalapa.ads.core.telemetry.TelemetrySink
import vn.kalapa.ads.miniapp.FeCreditMiniAppConfig
import vn.kalapa.ads.sample.SampleApplication
import vn.kalapa.ads.sample.data.MiniAppProfile
import vn.kalapa.ads.session.AdsSession
import vn.kalapa.ads.transport.AdsTransport

/** The origin the mini-app may load from; any other origin is refused by the SDK. */
private const val MINI_APP_ALLOWED_ORIGIN = "https://uat.moneyiq.vn"

/**
 * SDK step 2: build the config and start a session. Returns null when the config is invalid or the start fails.
 * A session is immutable: to change the profile or the transport, close it and start another.
 *
 * @param transport replaces the SDK's HTTP transport (the offline scenarios); null talks to the live ads API.
 * @param telemetry receives every event the SDK reports.
 */
internal suspend fun startAdsSession(
    application: Application,
    transport: AdsTransport?,
    profile: MiniAppProfile,
    telemetry: TelemetrySink,
): AdsSession? {
    val feCredit = (FeCreditMiniAppConfig.Builder().build() as? SdkResult.Success)?.value
    val config = AdsSdkConfig.Builder()
        .transport(transport)
        .miniAppPresenter((application as SampleApplication).miniAppPresenter)
        .miniAppAllowedOrigin(MINI_APP_ALLOWED_ORIGIN)
        .feCreditMiniApp(feCredit)
        // The profile fields are optional: a blank one is left out.
        .userId(profile.optionalUserId())
        .fullName(profile.optionalFullName())
        .phoneNumber(profile.optionalPhoneNumber())
        .amount(profile.optionalAmount())
        .period(profile.optionalPeriod())
        // Keeps impressions and clicks on disk until sent; without it a process death loses the unsent ones.
        .eventStorageDirectory(File(application.noBackupFilesDir, "kalapa-ads"))
        .environment(
            SdkEnvironment.Builder()
                .telemetry(telemetry)
                .logger(SdkLogger.Builder().minLevel(LogLevel.DEBUG).sink(LogcatSink("KalapaAds")).build())
                .build(),
        )
        .build()
    val started = (config as? SdkResult.Success)?.let { AdsSdk.start(it.value) }
    return (started as? SdkResult.Success)?.value
}
