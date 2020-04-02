package pt.gaps.epidemictracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ForegroundService extends Service {
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NotificationHelper.FOREGROUND_NOTIFICATION_ID,
                NotificationHelper.createForegroundNotification(this));
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        super.onDestroy();
    }
}
