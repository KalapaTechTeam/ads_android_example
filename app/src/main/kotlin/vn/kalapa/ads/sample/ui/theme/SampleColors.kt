package vn.kalapa.ads.sample.ui.theme

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import vn.kalapa.ads.sample.R

/** The sample's palette, read from `colors.xml`. The sample has one look: light. */
internal fun staticColorScheme(context: Context): ColorScheme = createLightColorScheme(context)

private fun createLightColorScheme(context: Context): ColorScheme =
    lightColorScheme(
        primary = context.color(R.color.sample_color_primary_light),
        onPrimary = context.color(R.color.sample_color_on_primary_light),
        primaryContainer = context.color(R.color.sample_color_primary_container_light),
        onPrimaryContainer = context.color(R.color.sample_color_on_primary_container_light),
        secondary = context.color(R.color.sample_color_secondary_light),
        onSecondary = context.color(R.color.sample_color_on_secondary_light),
        secondaryContainer = context.color(R.color.sample_color_secondary_container_light),
        onSecondaryContainer = context.color(R.color.sample_color_on_secondary_container_light),
        tertiary = context.color(R.color.sample_color_tertiary_light),
        onTertiary = context.color(R.color.sample_color_on_tertiary_light),
        tertiaryContainer = context.color(R.color.sample_color_tertiary_container_light),
        onTertiaryContainer = context.color(R.color.sample_color_on_tertiary_container_light),
        background = context.color(R.color.sample_color_background_light),
        onBackground = context.color(R.color.sample_color_on_background_light),
        surface = context.color(R.color.sample_color_surface_light),
        onSurface = context.color(R.color.sample_color_on_surface_light),
        surfaceVariant = context.color(R.color.sample_color_surface_variant_light),
        onSurfaceVariant = context.color(R.color.sample_color_on_surface_variant_light),
        surfaceContainerLowest = context.color(R.color.sample_color_surface_container_lowest_light),
        surfaceContainerLow = context.color(R.color.sample_color_surface_container_low_light),
        surfaceContainer = context.color(R.color.sample_color_surface_container_light),
        surfaceContainerHigh = context.color(R.color.sample_color_surface_container_high_light),
        surfaceContainerHighest = context.color(R.color.sample_color_surface_container_highest_light),
        outline = context.color(R.color.sample_color_outline_light),
        outlineVariant = context.color(R.color.sample_color_outline_variant_light),
        error = context.color(R.color.sample_color_error_light),
        onError = context.color(R.color.sample_color_on_error_light),
        errorContainer = context.color(R.color.sample_color_error_container_light),
        onErrorContainer = context.color(R.color.sample_color_on_error_container_light),
    )

private fun Context.color(resourceId: Int): Color = Color(getColor(resourceId))
