package vn.kalapa.ads.sample.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import vn.kalapa.ads.sample.R
import vn.kalapa.ads.sample.notification.MiniAppNotifications
import vn.kalapa.ads.sample.ui.theme.SampleSizes
import vn.kalapa.ads.sample.ui.theme.SampleSpacing

/** The UAT mini-app the sample opens by default; its creative and persona match the bundled sample responses. */
internal const val MINI_APP_UAT_URL = "https://uat.moneyiq.vn/mini-app?creative_id=P1-B&persona_id=P1"

/**
 * Opens a mini-app with no ad on screen, docked above the navigation bar like the Feed's controls. It opens the UAT
 * URL; the production default is a secondary switch, off unless chosen, because it may create real leads. The second
 * button sends a notification that opens the same mini-app when tapped, and a snackbar above the dock says what
 * happened (a Toast cuts a long message short).
 */
@Composable
internal fun MiniAppDock(enabled: Boolean, onOpen: (url: String?) -> Unit) {
    var production by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    fun say(message: Int, action: Int? = null, onAction: () -> Unit = {}) {
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result = snackbar.showSnackbar(
                message = context.getString(message),
                actionLabel = action?.let(context::getString),
                // Short unless there is an action to reach: it covers the form's last row while it shows.
                duration = if (action == null) SnackbarDuration.Short else SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) onAction()
        }
    }
    fun notifyLater() {
        MiniAppNotifications.schedule(context, if (production) null else MINI_APP_UAT_URL)
        say(R.string.notification_scheduled)
    }
    fun notificationsOff() = say(R.string.notification_denied, R.string.tab_settings) {
        // The per-app notification screen (channels included) starts at Android 8; before that, the app's details.
        val settings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
        }
        context.startActivity(settings)
    }
    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && MiniAppNotifications.enabled(context)) notifyLater() else notificationsOff()
    }
    val switchLabel: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier) {
            Text(stringResource(R.string.mini_app_production_switch), style = MaterialTheme.typography.bodyLarge)
            Text(
                stringResource(R.string.mini_app_production_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
    val openButton: @Composable (Modifier) -> Unit = { modifier ->
        Button(
            modifier = modifier.heightIn(min = SampleSizes.MinTouchTarget),
            enabled = enabled,
            onClick = { onOpen(if (production) null else MINI_APP_UAT_URL) },
        ) {
            Text(stringResource(if (production) R.string.mini_app_open_production else R.string.mini_app_open))
        }
    }
    val notifyButton: @Composable (Modifier) -> Unit = { modifier ->
        OutlinedButton(
            modifier = modifier.heightIn(min = SampleSizes.MinTouchTarget),
            onClick = {
                when {
                    MiniAppNotifications.enabled(context) -> notifyLater()
                    // Android 13+ asks once while the permission is missing; a turned-off app or channel, or an
                    // older Android, can only be fixed in the system settings.
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !MiniAppNotifications.appEnabled(context) ->
                        requestPermission.launch(MiniAppNotifications.PERMISSION)
                    else -> notificationsOff()
                }
            },
        ) {
            Text(stringResource(R.string.mini_app_notify))
        }
    }
    Column {
        SnackbarHost(snackbar, Modifier.padding(horizontal = SampleSpacing.Two))
        Surface(tonalElevation = SampleSpacing.Half) {
            BoxWithConstraints(
                Modifier.fillMaxWidth().padding(horizontal = SampleSpacing.Two, vertical = SampleSpacing.One),
            ) {
                if (maxWidth >= WIDE_DOCK) {
                    // Wide enough (a phone held sideways, a tablet): one row leaves the height to the form above.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SampleSpacing.Two),
                    ) {
                        switchLabel(Modifier.weight(1f))
                        Switch(checked = production, onCheckedChange = { production = it })
                        notifyButton(Modifier.widthIn(min = OPEN_BUTTON_MIN_WIDTH))
                        openButton(Modifier.widthIn(min = OPEN_BUTTON_MIN_WIDTH))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(SampleSpacing.One)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            switchLabel(Modifier.weight(1f))
                            Switch(checked = production, onCheckedChange = { production = it })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(SampleSpacing.One)) {
                            notifyButton(Modifier.weight(1f))
                            openButton(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/** The dock's content width (inside its padding) from which the switch and the button share one row. */
private val WIDE_DOCK = 600.dp
private val OPEN_BUTTON_MIN_WIDTH = 200.dp
