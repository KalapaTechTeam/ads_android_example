package vn.kalapa.ads.sample.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import vn.kalapa.ads.sample.MainActivity
import vn.kalapa.ads.sample.R

/**
 * A local notification that opens a mini-app when tapped, so the flow can be tried with the app open, in the
 * background or killed. The app is the notification's owner: the SDK only opens the mini-app, through
 * `session.openMiniApp(url)`, once [MainActivity] has handed it the URL from the tap.
 *
 * The payload is one extra, [EXTRA_URL]: an empty value opens the SDK's default (production) URL.
 */
internal object MiniAppNotifications {
    /** The Intent extra that carries the mini-app URL from the notification to [MainActivity]. */
    const val EXTRA_URL = "mini_app_url"

    /** The permission to ask for on Android 13+, where notifications are off until the user allows them. */
    const val PERMISSION = Manifest.permission.POST_NOTIFICATIONS

    private const val CHANNEL_ID = "mini_app"
    private const val DELAY_MS = 5_000L

    /** False when the user has not granted `POST_NOTIFICATIONS` (Android 13+) or has turned the app's notifications off. */
    fun appEnabled(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()

    /** [appEnabled] and the mini-app channel not turned off on its own (Android 8+; the channel exists after the first post). */
    fun enabled(context: Context): Boolean {
        if (!appEnabled(context)) return false
        val channel = NotificationManagerCompat.from(context).getNotificationChannelCompat(CHANNEL_ID)
        return channel == null || channel.importance != NotificationManagerCompat.IMPORTANCE_NONE
    }

    /** Posts the notification [DELAY_MS] from now, even if the app is closed by then. [url] `null` is the default URL. */
    fun schedule(context: Context, url: String?) {
        val intent = Intent(context, NotificationReceiver::class.java).putExtra(EXTRA_URL, url.orEmpty())
        val pending = PendingIntent.getBroadcast(context, nextId(), intent, PendingIntent.FLAG_IMMUTABLE)
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + DELAY_MS,
            pending,
        )
    }

    // The permission can be revoked between scheduling and posting, which `enabled` covers.
    @SuppressLint("MissingPermission")
    fun show(context: Context, url: String?) {
        if (!enabled(context)) return
        ensureChannel(context)
        // NEW_TASK + CLEAR_TOP with MainActivity's singleTop: an open app gets onNewIntent, a closed one starts fresh.
        val open = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra(EXTRA_URL, url.orEmpty())
        val id = nextId()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_body))
            .setContentIntent(PendingIntent.getActivity(context, id, open, PendingIntent.FLAG_IMMUTABLE))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_HIGH,
        )
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    // A distinct id per notification keeps each PendingIntent, and so each URL, its own.
    private fun nextId(): Int = SystemClock.elapsedRealtime().toInt()
}
