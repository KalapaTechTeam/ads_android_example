package vn.kalapa.ads.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import vn.kalapa.ads.sample.ads.AdsViewModel
import vn.kalapa.ads.sample.notification.MiniAppNotifications
import vn.kalapa.ads.sample.ui.SampleApp
import vn.kalapa.ads.sample.ui.theme.SampleTheme

/** The only Activity: hosts the feed, the SDK events and the settings. */
internal class MainActivity : ComponentActivity() {
    private val viewModel: AdsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The sample is light only, so the system bars keep dark icons even when the device is in dark mode.
        val light = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = light, navigationBarStyle = light)
        // A recreation (rotation, process death) is restored with the intent that launched it: do not open again.
        if (savedInstanceState == null) openMiniAppFrom(intent)
        setContent {
            SampleTheme {
                SampleApp(viewModel)
            }
        }
    }

    /** The app is already open and a notification was tapped: `launchMode="singleTop"` delivers it here. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openMiniAppFrom(intent)
    }

    private fun openMiniAppFrom(intent: Intent) {
        if (!intent.hasExtra(MiniAppNotifications.EXTRA_URL)) return
        viewModel.openFromNotification(intent.getStringExtra(MiniAppNotifications.EXTRA_URL)?.ifEmpty { null })
    }
}
