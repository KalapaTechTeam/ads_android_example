# Kalapa Ads SDK: Android sample

A runnable Android app that integrates the Kalapa Ads SDK from Maven Central: a feed with a banner and a native ad
slot, the SDK's events, a settings screen and a button that opens the mini-app. Copy the integration points listed
below into your own app.

The SDK itself is Views/XML and needs no Compose. This sample's UI is written in Jetpack Compose only because that is
how it is laid out; the ad views are embedded with `AndroidView`, and a Views host declares them in XML instead.

| Item | Value |
|---|---|
| SDK | `vn.kalapa:ads-bom:0.1.0` (`ads-ui-view` includes the headless `ads`) |
| minSdk / compileSdk | 24 / 36 |
| Android Gradle Plugin / Kotlin / Gradle | 9.4.1 / 2.4.20 / 9.7.1 (wrapper included) |
| JDK | 17 or newer |

## Run it

Open the folder in Android Studio and run the `app` configuration, or with a device or emulator attached:

```bash
./gradlew :app:installDebug
./gradlew :app:testDebugUnitTest   # JVM unit tests
```

## What is in the app

- **Feed**: 12 posts with a banner slot after the third and a native slot after the tenth. Above the navigation bar:
  the offline scenario, a **Live** switch and **Reload**.
- **SDK events**: everything the SDK reported to the host's telemetry sink; tap an event for its attributes.
- **Settings**: the mini-app profile (identity number, name, phone, amount, loan term), the app language, and
  **Open mini-app**.

The offline scenarios (normal ads, bad data, network problems, longest text) answer from sample responses bundled in
`app/src/main/assets/recommendations/`, so the app runs without the ads API. Turn **Live** on to use the real API
(`https://ads.kalapa.vn`). The mini-app opens the UAT page (`uat.moneyiq.vn`) by default; the production switch is off
unless chosen, because it may create real leads.

## The SDK integration points

| Step | Where |
|---|---|
| Add the SDK: the BOM, then `ads-ui-view` without a version | [app/build.gradle.kts](app/build.gradle.kts) |
| 1. Create the mini-app presenter once in your `Application` | [SampleApplication.kt](app/src/main/kotlin/vn/kalapa/ads/sample/SampleApplication.kt) |
| 2. Build `AdsSdkConfig` (profile, transport, telemetry) and `AdsSdk.start(config)` | [ads/AdsSessionStarter.kt](app/src/main/kotlin/vn/kalapa/ads/sample/ads/AdsSessionStarter.kt) |
| 3. `createController(adUnitId, AdSlot)`, `setPlacement`, `load()`, `dispose()`; own the session in a ViewModel | [ads/FeedSlots.kt](app/src/main/kotlin/vn/kalapa/ads/sample/ads/FeedSlots.kt), [ads/AdsViewModel.kt](app/src/main/kotlin/vn/kalapa/ads/sample/ads/AdsViewModel.kt) |
| 4. `BannerAdView` / `NativeAdView` with `setController` | [ui/feed/FeedScreen.kt](app/src/main/kotlin/vn/kalapa/ads/sample/ui/feed/FeedScreen.kt) |
| 5. `session.openMiniApp(url)` | `AdsViewModel.openMiniApp` |

Replace the two ad unit ids in `ads/FeedSlots.kt` with the ones Kalapa gives you. R8 needs no extra rules: the SDK ships
its own. The manifest needs no `INTERNET` permission or activity declaration: `ads-ui-view` merges them.

### In a Views/XML app

Steps 1 to 3 and 5 are the same. Only step 4 differs: declare the view in the layout and give it the controller. The view
stays `GONE` until its ad is ready, so a margin on it takes no space while there is nothing to show.

```xml
<vn.kalapa.ads.adsui.banner.BannerAdView
    android:id="@+id/banner_ad"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginVertical="@dimen/kalapa_ads_ad_margin" />
```

```kotlin
// The controller comes from the session your ViewModel owns, as in AdsViewModel.
findViewById<BannerAdView>(R.id.banner_ad).setController(controller)
```

`NativeAdView` (`vn.kalapa.ads.adsui.nativead`) is declared the same way. Each view draws only its own kind of creative,
so pick the one that matches `state.ad?.creative`, as `FeedScreen.kt` does. This sample runs on Compose and does not
include a Views screen, so the XML above follows the SDK's own guide and is not exercised here.

## Package map (`vn.kalapa.ads.sample`)

| Package | Content |
|---|---|
| root | `SampleApplication`, `MainActivity` |
| `ads` | The SDK integration: session starter, feed slot controllers, `AdsViewModel` (owns the session, rebuilds it when Live or the profile changes, survives configuration changes) |
| `data` | Offline scenarios and their transport, event log, mini-app profile and its store, language setting |
| `ui` | `SampleApp` shell; `feed`, `events`, `settings`, `navigation` (floating bar) and `theme` |

## Learn more

The full guide, error codes and theming are in the SDK documentation. Contact Kalapa for the ad unit ids and for access
to the SDK source and docs.
