package vn.kalapa.ads.sample.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data item representing a destination within [SampleFloatingNavBar].
 */
internal data class SampleNavItem(
    internal val id: String,
    internal val selected: Boolean,
    internal val onClick: () -> Unit,
    internal val selectedIcon: ImageVector,
    internal val unselectedIcon: ImageVector,
    internal val label: String,
    internal val badgeCount: Int = 0,
    internal val contentDescription: String? = null,
)
