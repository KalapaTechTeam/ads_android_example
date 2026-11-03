package vn.kalapa.ads.sample.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.data.Scenario
import vn.kalapa.ads.sample.data.ScenarioGroup
import androidx.compose.ui.text.style.TextOverflow
import vn.kalapa.ads.sample.ui.theme.SampleSizes
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

/**
 * Scenario, Live and Reload, docked just above the navigation bar so a thumb reaches them. The
 * scenario menu opens upward from here, grouped, each case with a line on what the slot should do. With Live on the offline scenario has no effect, so its
 * button is disabled.
 */
@Composable
internal fun ControlsDock(
    scenario: Scenario,
    live: Boolean,
    onScenario: (Scenario) -> Unit,
    onLive: (Boolean) -> Unit,
    onReload: () -> Unit,
) {
    Surface(tonalElevation = SampleSpacing.Half) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = SampleSpacing.Two, vertical = SampleSpacing.One),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SampleSpacing.One),
        ) {
            var open by remember { mutableStateOf(false) }
            Box(Modifier.weight(1f)) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().heightIn(min = SampleSizes.MinTouchTarget),
                    enabled = !live,
                    onClick = { open = true },
                ) {
                    Text(stringResource(scenario.label), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = stringResource(R.string.scenario))
                }
                DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                    ScenarioGroup.entries.forEach { group ->
                        Text(
                            stringResource(group.title),
                            modifier = Modifier.padding(horizontal = SampleSpacing.Two, vertical = SampleSpacing.One),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Scenario.entries.filter { it.group == group }.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(stringResource(item.label))
                                        Text(
                                            stringResource(item.hint),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                },
                                leadingIcon = {
                                    if (item == scenario) Icon(Icons.Filled.Check, contentDescription = null)
                                },
                                onClick = {
                                    open = false
                                    onScenario(item)
                                },
                            )
                        }
                    }
                }
            }
            Text(stringResource(R.string.live))
            Switch(checked = live, onCheckedChange = onLive)
            FilledTonalIconButton(onClick = onReload) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.reload))
            }
        }
    }
}
