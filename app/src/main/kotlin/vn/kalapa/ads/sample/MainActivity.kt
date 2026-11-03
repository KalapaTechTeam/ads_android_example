package vn.kalapa.ads.sample

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import vn.kalapa.ads.sample.ui.SampleApp
import vn.kalapa.ads.sample.ui.theme.SampleTheme

/** The only Activity: hosts the feed, the SDK events and the settings. */
internal class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The sample is light only, so the system bars keep dark icons even when the device is in dark mode.
        val light = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = light, navigationBarStyle = light)
        setContent {
            SampleTheme {
                SampleApp()
            }
        }
    }
}
