package vn.kalapa.ads.sample.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.ui.theme.SampleSizes
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

/** The UAT mini-app the sample opens by default; its creative and persona match the bundled sample responses. */
internal const val MINI_APP_UAT_URL = "https://uat.moneyiq.vn/mini-app?creative_id=P1-B&persona_id=P1"

/**
 * Opens a mini-app with no ad on screen, docked above the navigation bar like the Feed's controls. It opens the UAT
 * URL; the production default is a secondary switch, off unless chosen, because it may create real leads.
 */
@Composable
internal fun MiniAppDock(enabled: Boolean, onOpen: (url: String?) -> Unit) {
    var production by rememberSaveable { mutableStateOf(false) }
    Surface(tonalElevation = SampleSpacing.Half) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = SampleSpacing.Two, vertical = SampleSpacing.One),
            verticalArrangement = Arrangement.spacedBy(SampleSpacing.One),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.mini_app_production_switch),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        stringResource(R.string.mini_app_production_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Switch(checked = production, onCheckedChange = { production = it })
            }
            Button(
                modifier = Modifier.fillMaxWidth().heightIn(min = SampleSizes.MinTouchTarget),
                enabled = enabled,
                onClick = { onOpen(if (production) null else MINI_APP_UAT_URL) },
            ) {
                Text(
                    stringResource(if (production) R.string.mini_app_open_production else R.string.mini_app_open),
                )
            }
        }
    }
}
