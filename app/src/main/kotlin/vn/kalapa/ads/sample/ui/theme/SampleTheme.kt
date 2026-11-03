package vn.kalapa.ads.sample.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * The sample's own look: a static light palette (so screenshots repeat), typography and shapes. Creatives bring
 * their own fixed colours, and the SDK's screens do not read this theme.
 */
@Composable
internal fun SampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = staticColorScheme(LocalContext.current),
        typography = SampleTypography,
        shapes = SampleShapes,
        content = content,
    )
}
