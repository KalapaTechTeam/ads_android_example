package vn.kalapa.ads.sample.ui.theme

import androidx.compose.ui.unit.dp

/** The 8-point spacing scale the sample's screens share. */
internal object SampleSpacing {
    val Half = 4.dp
    val One = 8.dp
    val Two = 16.dp
    val Three = 24.dp
}

internal object SampleSizes {
    val MinTouchTarget = 48.dp
}

/** Only the dimensions needed by this sample's shared components. */
internal object SampleDimens {
    val spaceXSmall = SampleSpacing.Half
    val spaceSmall = SampleSpacing.One
    val spaceMediumSmall = SampleSpacing.Two
    val minTouchTarget = SampleSizes.MinTouchTarget
    val iconSizeMedium = 24.dp
    val radiusPill = 100.dp
    val elevationMedium = 2.dp
    val maxNavBarWidth = 600.dp
    val maxContentWidth = 720.dp
}
