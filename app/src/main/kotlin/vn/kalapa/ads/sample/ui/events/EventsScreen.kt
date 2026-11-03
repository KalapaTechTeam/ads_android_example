package vn.kalapa.ads.sample.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.data.SampleEvent
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

@Composable
internal fun EventsScreen(events: List<SampleEvent>, onClear: () -> Unit, modifier: Modifier = Modifier) {
    if (events.isEmpty()) {
        Box(modifier.fillMaxSize().padding(SampleSpacing.Three), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.events_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }
    val clock = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.ROOT) }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = SampleSpacing.Two, vertical = SampleSpacing.One),
        verticalArrangement = Arrangement.spacedBy(SampleSpacing.One),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pluralStringResource(R.plurals.events_count, events.size, events.size),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                )
                TextButton(onClick = onClear) { Text(stringResource(R.string.events_clear)) }
            }
        }
        items(events, key = { it.id }) { EventCard(it, clock.format(Date(it.timeMillis))) }
    }
}

/** One event: its name and a one-line summary; a tap shows every attribute the SDK reported. */
@Composable
private fun EventCard(event: SampleEvent, time: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(SampleSpacing.Two), verticalArrangement = Arrangement.spacedBy(SampleSpacing.Half)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SampleSpacing.One),
            ) {
                Box(Modifier.size(SampleSpacing.One).clip(CircleShape).background(categoryColor(event.name)))
                Text(event.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                Text(time, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
            }
            Text(
                summary(event),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (expanded) {
                Text(
                    event.attributes.entries.sortedBy { it.key }.joinToString("\n") { "${it.key} = ${it.value}" },
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
private fun categoryColor(name: String): Color = when {
    name.endsWith("click") -> MaterialTheme.colorScheme.primary
    name.contains("mini_app") -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.secondary
}

private fun summary(event: SampleEvent): String = listOfNotNull(
    event.attributes["placement_id"],
    event.attributes["ad_id"]?.take(AD_ID_PREFIX),
    event.attributes["result"],
    event.attributes["close_reason"],
    event.attributes["reason"],
    event.attributes["error_code"]?.let { "#$it" },
).joinToString(" · ")

private const val AD_ID_PREFIX = 8
