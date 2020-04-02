package pt.gaps.epidemictracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

class NotificationHelper {
    static final int FOREGROUND_NOTIFICATION_ID = 1;

    @RequiresApi(Build.VERSION_CODES.O)
    static String createForegroundNotificationChannel(Context c) {
        String channelId = "persistent";
        NotificationChannel chan = new NotificationChannel(
                channelId, "Persistent notifications", NotificationManager.IMPORTANCE_LOW);
        chan.setLockscreenVisibility(Notification.BADGE_ICON_NONE);
        chan.setShowBadge(false);
        NotificationManager service = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    static Notification createForegroundNotification(Context c) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = NotificationHelper.createForegroundNotificationChannel(c);
            builder = new Notification.Builder(c, channelId);
        } else {
            builder = new Notification.Builder(c);
        }
        return builder
                .setContentTitle("Tracking")
                .setContentText("This device is discoverable to others nearby.")
                .setPriority(Notification.PRIORITY_LOW)
                .build();
    }
}
