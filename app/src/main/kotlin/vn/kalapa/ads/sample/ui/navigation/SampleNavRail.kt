package vn.kalapa.ads.sample.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import vn.kalapa.ads.sample.R

/**
 * The same destinations as [SampleFloatingNavBar], as a rail at the start edge: a phone held sideways has too little
 * height for a bar along the bottom. The caller applies the window insets.
 */
@Composable
internal fun SampleNavRail(items: List<SampleNavItem>, modifier: Modifier = Modifier) {
    NavigationRail(modifier = modifier, windowInsets = WindowInsets(0, 0, 0, 0)) {
        Spacer(Modifier.weight(1f))
        items.forEach { item ->
            val badge = if (item.badgeCount > 0) {
                pluralStringResource(R.plurals.nav_badge_count, item.badgeCount, item.badgeCount)
            } else {
                null
            }
            val name = item.contentDescription ?: item.label
            NavigationRailItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) Badge { Text(if (item.badgeCount > 99) "99+" else "${item.badgeCount}") }
                        },
                    ) {
                        Icon(if (item.selected) item.selectedIcon else item.unselectedIcon, contentDescription = null)
                    }
                },
                label = { Text(item.label) },
                modifier = Modifier.semantics { contentDescription = if (badge != null) "$name, $badge" else name },
            )
        }
        Spacer(Modifier.weight(1f))
    }
}
