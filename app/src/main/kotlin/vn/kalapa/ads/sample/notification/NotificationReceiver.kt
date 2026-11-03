package vn.kalapa.ads.sample.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Posts the notification [MiniAppNotifications.schedule] asked for; runs even when the app's process was killed. */
internal class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        MiniAppNotifications.show(context, intent.getStringExtra(MiniAppNotifications.EXTRA_URL)?.ifEmpty { null })
    }
}
